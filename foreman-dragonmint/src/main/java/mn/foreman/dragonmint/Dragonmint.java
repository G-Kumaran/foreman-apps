package mn.foreman.dragonmint;

import mn.foreman.dragonmint.json.Summary;
import mn.foreman.io.Query;
import mn.foreman.model.AbstractMiner;
import mn.foreman.model.error.MinerException;
import mn.foreman.model.miners.FanInfo;
import mn.foreman.model.miners.MinerStats;
import mn.foreman.model.miners.Pool;
import mn.foreman.model.miners.asic.Asic;
import mn.foreman.util.PoolUtils;

import java.math.BigDecimal;
import java.util.List;

/**
 * <h1>Overview</h1>
 *
 * A {@link Dragonmint} represents a remote dragonmint instance.
 *
 * <p>This class relies on the dragonmint-api being enabled and configured to
 * allow the server that this application is running on to access it.  If this
 * application is running on the rig server, only localhost connections need to
 * be allowed.</p>
 *
 * <p>This class currently queries:</p>
 *
 * <pre>
 *     GET http://{@link #apiIp}:{@link #apiPort}/api/summary
 * </pre>
 */
public class Dragonmint
        extends AbstractMiner {

    /** The API password. */
    private final String password;

    /** The API username. */
    private final String username;

    /**
     * Constructor.
     *
     * @param name     The name.
     * @param apiIp    The API IP.
     * @param apiPort  The API port.
     * @param username The username.
     * @param password The password.
     */
    Dragonmint(
            final String name,
            final String apiIp,
            final int apiPort,
            final String username,
            final String password) {
        super(
                name,
                apiIp,
                apiPort);
        this.username = username;
        this.password = password;
    }

    @Override
    protected void addStats(
            final MinerStats.Builder statsBuilder)
            throws MinerException {
        final Summary summary =
                Query.restQuery(
                        this.apiIp,
                        this.apiPort,
                        "/api/summary",
                        this.username,
                        this.password,
                        Summary.class);
        addAsics(
                statsBuilder,
                summary.devs,
                summary.hardware);
        addPools(
                statsBuilder,
                summary.pools);
    }

    @Override
    protected String addToString() {
        return String.format(
                ", username=%s, password=%s",
                this.username,
                this.password);
    }

    /**
     * Adds an ASIC from the provided devices and hardware.
     *
     * @param statsBuilder The stats builder.
     * @param devs         The devices.
     * @param hardware     The hardware information.
     */
    private static void addAsics(
            final MinerStats.Builder statsBuilder,
            final List<Summary.Dev> devs,
            final Summary.Hardware hardware) {
        final Asic.Builder asicBuilder =
                new Asic.Builder();
        asicBuilder
                .setName("dragonmint")
                .setHashRate(toHashRate(devs))
                .setFanInfo(
                        new FanInfo.Builder()
                                .setCount(1)
                                .addSpeed(hardware.fanSpeed)
                                .setSpeedUnits("%")
                                .build());
        for (final Summary.Dev dev : devs) {
            asicBuilder.addTemp(dev.temperature);
        }
        statsBuilder.addAsic(asicBuilder.build());
    }

    /**
     * Adds the {@link Pool}.
     *
     * @param statsBuilder The builder to update.
     * @param pool         The {@link Pool}.
     */
    private static void addPool(
            final MinerStats.Builder statsBuilder,
            final Summary.Pool pool) {
        statsBuilder.addPool(
                new Pool.Builder()
                        .setName(PoolUtils.sanitizeUrl(pool.url))
                        .setPriority(pool.priority)
                        .setStatus(
                                !"Disabled".equals(pool.status),
                                "Alive".equals(pool.status))
                        .setCounts(
                                pool.accepted,
                                pool.rejected,
                                pool.stale)
                        .build());
    }

    /**
     * Adds the {@link Pool Pools}.
     *
     * @param statsBuilder The builder to update.
     * @param pools        The {@link Pool Pools}.
     */
    private static void addPools(
            final MinerStats.Builder statsBuilder,
            final List<Summary.Pool> pools) {
        pools.forEach(
                pool ->
                        addPool(
                                statsBuilder,
                                pool));
    }

    /**
     * Adds up the hash rates from the provided devices.
     *
     * @param devs The devices.
     *
     * @return The hash rate.
     */
    private static BigDecimal toHashRate(
            final List<Summary.Dev> devs) {
        return devs
                .stream()
                .map((dev) -> dev.hashRate)
                .map((value) ->
                        value.multiply(
                                new BigDecimal(1000 * 1000)))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
package mn.foreman.excavator;

import mn.foreman.excavator.json.*;
import mn.foreman.excavator.method.AlgorithmMethod;
import mn.foreman.excavator.method.DevicesMethod;
import mn.foreman.excavator.method.Method;
import mn.foreman.excavator.method.SubscribeMethod;
import mn.foreman.io.ApiRequest;
import mn.foreman.io.ApiRequestImpl;
import mn.foreman.io.Connection;
import mn.foreman.io.ConnectionFactory;
import mn.foreman.model.Miner;
import mn.foreman.model.miners.FanInfo;
import mn.foreman.model.miners.MinerStats;
import mn.foreman.model.miners.Pool;
import mn.foreman.model.miners.rig.FreqInfo;
import mn.foreman.model.miners.rig.Gpu;
import mn.foreman.model.miners.rig.Rig;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * <h1>Overview</h1>
 *
 * <p>An {@link Excavator} represents a remote excavator instance.</p>
 *
 * <p>This class relies on the excavator-api being enabled and configured to
 * allow the server that this application is running on to access it.  If this
 * application is running on the rig server, only localhost connections need to
 * be allowed.</p>
 *
 * <p>This class currently queries the following methods via JSON RPC over TCP
 * (the HTTP interface is not used):</p>
 *
 * <ul>
 *     <li>subscribe.info</li>
 *     <li>devices.get</li>
 *     <li>algorithm.list</li>
 * </ul>
 */
public class Excavator
        implements Miner {

    /** The logger for this class. */
    private static final Logger LOG =
            LoggerFactory.getLogger(Excavator.class);

    /** The API IP. */
    private final String apiIp;

    /** The API port. */
    private final int apiPort;

    /** The miner name. */
    private final String name;

    /**
     * Constructor.
     *
     * @param name    The name.
     * @param apiIp   The API IP.
     * @param apiPort The API port.
     */
    Excavator(
            final String name,
            final String apiIp,
            final int apiPort) {
        Validate.notEmpty(
                name,
                "name cannot be empty");
        Validate.notEmpty(
                apiIp,
                "apiIp cannot be empty");
        Validate.isTrue(
                apiPort > 0,
                "apiPort must be > 0");
        this.name = name;
        this.apiIp = apiIp;
        this.apiPort = apiPort;
    }

    @Override
    public MinerStats getStats() {
        LOG.debug("Obtaining stats from {}-{}:{}",
                this.name,
                this.apiIp,
                this.apiPort);

        final MinerStats.Builder statsBuilder =
                new MinerStats.Builder()
                        .setApiIp(this.apiIp)
                        .setApiPort(this.apiPort)
                        .setName(this.name);

        addRig(
                statsBuilder,
                addPools(statsBuilder));

        return statsBuilder.build();
    }

    @Override
    public String toString() {
        return String.format(
                "%s [ name=%s, apiIp=%s, apiPort=%d ]",
                getClass().getSimpleName(),
                this.name,
                this.apiIp,
                this.apiPort);
    }

    /**
     * Adds the provided {@link Devices.Device} as a {@link Gpu} to the {@link
     * Rig.Builder builder}.
     *
     * @param builder The builder to update.
     * @param device  The {@link Devices.Device}.
     */
    private void addGpu(
            final Rig.Builder builder,
            final Devices.Device device) {
        builder
                .addGpu(
                        new Gpu.Builder()
                                .setName(device.name)
                                .setIndex(device.deviceId)
                                .setBus(device.details.busId)
                                .setTemp(device.gpuTemp)
                                .setFans(
                                        new FanInfo.Builder()
                                                .setCount(1)
                                                .addSpeed(device.gpuFanSpeed)
                                                .setSpeedUnits("%")
                                                .build())
                                .setFreqInfo(
                                        new FreqInfo.Builder()
                                                .setFreq(device.gpuClockCoreMax)
                                                .setMemFreq(device.gpuClockMemory)
                                                .build())
                                .build());
    }

    /**
     * Adds a {@link Pool} to the provided {@link MinerStats.Builder} using the
     * provided {@link Subscribe} and {@link Algorithms}.
     *
     * @param builder    The builder to update.
     * @param subscribe  The {@link Subscribe}.
     * @param algorithms The {@link Algorithms}.
     *
     * @return The hash rate.
     */
    private BigDecimal addPools(
            final MinerStats.Builder builder,
            final Subscribe subscribe,
            final Algorithms algorithms) {
        int totalShares = 0;
        int totalAccepted = 0;
        int totalRejected = 0;
        BigDecimal hashRate = BigDecimal.ZERO;
        for (final Algorithms.Algorithm algorithm : algorithms.algorithms) {
            totalShares += algorithm.sentShares;
            totalAccepted += algorithm.acceptedShares;
            totalRejected += algorithm.rejectedShares;
            hashRate = hashRate.add(algorithm.speed);
        }
        builder.addPool(
                new Pool.Builder()
                        .setName(subscribe.address)
                        .setPriority(0)
                        .setStatus(
                                true,
                                subscribe.connected)
                        .setCounts(
                                totalAccepted,
                                totalRejected,
                                totalShares - totalAccepted - totalRejected)
                        .build());
        return hashRate;
    }

    /**
     * Queries for and adds {@link Pool pools} to the provided {@link
     * MinerStats.Builder}.
     *
     * @param builder The builder to update.
     *
     * @return The current hash rate.
     */
    private BigDecimal addPools(final MinerStats.Builder builder) {
        BigDecimal hashRate = BigDecimal.ZERO;
        final Optional<Subscribe> subscribeOptional =
                query(
                        SubscribeMethod.INFO,
                        Subscribe.class);
        final Optional<Algorithms> algorithmsOptional =
                query(
                        AlgorithmMethod.LIST,
                        Algorithms.class);
        if (subscribeOptional.isPresent() && algorithmsOptional.isPresent()) {
            hashRate =
                    addPools(
                            builder,
                            subscribeOptional.get(),
                            algorithmsOptional.get());
        } else {
            LOG.warn("Failed to obtain both 'subscribe' and 'algorithms'");
        }
        return hashRate;
    }

    /**
     * Queries for and adds a {@link Rig} to the provided {@link
     * MinerStats.Builder}.
     *
     * @param builder  The builder to update.
     * @param hashRate The hash rate.
     */
    private void addRig(
            final MinerStats.Builder builder,
            final BigDecimal hashRate) {
        query(DevicesMethod.GET, Devices.class)
                .ifPresent((devices) -> {
                    final Rig.Builder rigBuilder =
                            new Rig.Builder()
                                    .setName("excavator")
                                    .setHashRate(hashRate);
                    devices.devices.forEach(
                            (device) ->
                                    addGpu(
                                            rigBuilder,
                                            device));
                    builder.addRig(rigBuilder.build());
                });
    }

    /**
     * Queries the API.
     *
     * @return The response.
     */
    private <T extends Response> Optional<T> query(
            final Method method,
            final Class<T> responseClass,
            final String... params) {
        T response = null;

        try {
            final ObjectMapper objectMapper =
                    new ObjectMapper();

            final String requestJson =
                    objectMapper
                            .writeValueAsString(
                                    new Request(
                                            1,
                                            method.toMethod(),
                                            Arrays.asList(params)));
            LOG.debug("Sending request ({}) to {}:{}",
                    requestJson,
                    this.apiIp,
                    this.apiPort);

            final ApiRequest request =
                    new ApiRequestImpl(
                            this.apiIp,
                            this.apiPort,
                            requestJson);

            final Connection connection =
                    ConnectionFactory.createJsonConnection(
                            request);
            connection.query();

            if (request.waitForCompletion(
                    10,
                    TimeUnit.SECONDS)) {
                response =
                        objectMapper.readValue(
                                request.getResponse(),
                                responseClass);
            }
        } catch (final IOException ioe) {
            LOG.warn("Exception occurred while querying",
                    ioe);
        }

        return Optional.ofNullable(response);
    }
}
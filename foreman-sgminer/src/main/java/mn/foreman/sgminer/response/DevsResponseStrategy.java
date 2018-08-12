package mn.foreman.sgminer.response;

import mn.foreman.cgminer.PoolsResponseStrategy;
import mn.foreman.cgminer.ResponseStrategy;
import mn.foreman.cgminer.request.CgMinerCommand;
import mn.foreman.cgminer.response.CgMinerResponse;
import mn.foreman.model.miners.FanInfo;
import mn.foreman.model.miners.MinerStats;
import mn.foreman.model.miners.rig.FreqInfo;
import mn.foreman.model.miners.rig.Gpu;
import mn.foreman.model.miners.rig.Rig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A {@link PoolsResponseStrategy} provides a {@link ResponseStrategy}
 * implementation that's capable of parsing a {@link CgMinerCommand#DEVS}
 * response from an sgminer.
 */
public class DevsResponseStrategy
        implements ResponseStrategy {

    /** The logger for this class. */
    private static final Logger LOG =
            LoggerFactory.getLogger(DevsResponseStrategy.class);

    @Override
    public void processResponse(
            final MinerStats.Builder builder,
            final CgMinerResponse response) {
        if (response.hasValues()) {
            final List<Map<String, String>> values = response.getValues();
            if (hasGpus(values)) {
                addRig(
                        values,
                        builder);
            }
        } else {
            LOG.debug("Received an empty response");
        }
    }

    /**
     * Adds all of the GPUs from the provided values.
     *
     * @param values       The values.
     * @param statsBuilder The builder.
     */
    private static void addRig(
            final List<Map<String, String>> values,
            final MinerStats.Builder statsBuilder) {
        final List<Map<String, String>> gpuValues =
                values
                        .stream()
                        .filter((map) -> map.containsKey("GPU"))
                        .collect(Collectors.toList());
        final Rig.Builder rigBuilder =
                new Rig.Builder()
                        .setHashRate(toRate(gpuValues));
        gpuValues
                .stream()
                .map(DevsResponseStrategy::toGpu)
                .forEach(rigBuilder::addGpu);
        statsBuilder.addRig(rigBuilder.build());
    }

    /**
     * Checks to see if the values have GPUs.
     *
     * @param values The values.
     *
     * @return Whether or not GPUs exist.
     */
    private static boolean hasGpus(
            final List<Map<String, String>> values) {
        return values
                .stream()
                .anyMatch((map) -> map.containsKey("GPU"));
    }

    /**
     * Converts the values to a {@link Gpu}.
     *
     * @param values The values.
     *
     * @return The {@link Gpu}.
     */
    private static Gpu toGpu(
            final Map<String, String> values) {
        return new Gpu.Builder()
                .setName("GPU " + values.get("GPU"))
                .setIndex(values.get("GPU"))
                .setBus(0)
                .setTemp(values.get("Temperature"))
                .setFans(
                        new FanInfo.Builder()
                                .setCount(1)
                                .addSpeed(values.get("Fan Percent"))
                                .setSpeedUnits("%")
                                .build())
                .setFreqInfo(
                        new FreqInfo.Builder()
                                .setFreq(values.get("GPU Clock"))
                                .setMemFreq(values.get("Memory Clock"))
                                .build())
                .build();
    }

    /**
     * Extracts the hash rate.
     *
     * @param values The values.
     *
     * @return The rate.
     */
    private static BigDecimal toRate(
            final List<Map<String, String>> values) {
        return values
                .stream()
                .map((map) -> new BigDecimal(map.get("MHS 5s")))
                .map((value) ->
                        value.multiply(
                                new BigDecimal(1000 * 1000)))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
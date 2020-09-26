package mn.foreman.spondoolies;

import mn.foreman.cgminer.*;
import mn.foreman.cgminer.request.CgMinerCommand;
import mn.foreman.cgminer.request.CgMinerRequest;
import mn.foreman.model.Miner;
import mn.foreman.model.MinerFactory;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

/**
 * A {@link MinerFactory} implementation that parses a configuration and creates
 * a {@link Miner} that will query a spondoolies miner.
 */
public class SpondooliesFactory
        implements MinerFactory {

    @Override
    public Miner create(final Map<String, Object> config) {
        final Context cgContext = new Context();
        final ResponseStrategy responseStrategy =
                new AggregatingResponseStrategy<>(
                        ImmutableMap.of(
                                "SUMMARY",
                                (values, builder, context) ->
                                        SpondooliesUtils.updateSummary(
                                                values,
                                                builder),
                                "STATS",
                                (values, builder, context) ->
                                        SpondooliesUtils.updateStats(
                                                values,
                                                builder)),
                        () -> null,
                        cgContext);
        return new CgMiner.Builder()
                .setApiIp(config.get("apiIp").toString())
                .setApiPort(config.get("apiPort").toString())
                .addRequest(
                        new CgMinerRequest.Builder()
                                .setCommand(CgMinerCommand.POOLS)
                                .build(),
                        new PoolsResponseStrategy(
                                new MrrRigIdCallback(cgContext)))
                .addRequest(
                        new CgMinerRequest.Builder()
                                .setCommand(CgMinerCommand.SUMMARY)
                                .build(),
                        responseStrategy)
                .addRequest(
                        new CgMinerRequest.Builder()
                                .setCommand(CgMinerCommand.STATS)
                                .build(),
                        responseStrategy)
                .build();
    }
}

package mn.foreman.aixin;

import mn.foreman.aixin.response.StatsResponseStrategy;
import mn.foreman.cgminer.CgMiner;
import mn.foreman.cgminer.Context;
import mn.foreman.cgminer.MrrRigIdCallback;
import mn.foreman.cgminer.PoolsResponseStrategy;
import mn.foreman.cgminer.request.CgMinerCommand;
import mn.foreman.cgminer.request.CgMinerRequest;
import mn.foreman.model.Miner;
import mn.foreman.model.MinerFactory;

import java.util.Map;

/**
 * A {@link MinerFactory} implementation that parses a configuration and creates
 * a {@link Miner} that will query an aixin.
 */
public class AixinFactory
        implements MinerFactory {

    @Override
    public Miner create(final Map<String, Object> config) {
        final Context context = new Context();
        return new CgMiner.Builder()
                .setApiIp(config.get("apiIp").toString())
                .setApiPort(config.get("apiPort").toString())
                .addRequest(
                        new CgMinerRequest.Builder()
                                .setCommand(CgMinerCommand.POOLS)
                                .build(),
                        new PoolsResponseStrategy(
                                new MrrRigIdCallback(
                                        context)))
                .addRequest(
                        new CgMinerRequest.Builder()
                                .setCommand(CgMinerCommand.STATS)
                                .build(),
                        new StatsResponseStrategy(context))
                .build();
    }
}
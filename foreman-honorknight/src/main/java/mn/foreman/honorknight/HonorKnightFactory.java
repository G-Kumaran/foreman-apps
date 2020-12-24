package mn.foreman.honorknight;

import mn.foreman.cgminer.*;
import mn.foreman.cgminer.request.CgMinerCommand;
import mn.foreman.cgminer.request.CgMinerRequest;
import mn.foreman.honorknight.response.StatsResponseStrategy;
import mn.foreman.model.Miner;
import mn.foreman.model.MinerFactory;

import java.util.List;
import java.util.Map;

/**
 * A {@link MinerFactory} implementation that parses a configuration and creates
 * a {@link Miner} that will query a honorknight.
 */
public class HonorKnightFactory
        extends CgMinerFactory {

    @Override
    protected Miner create(
            final String apiIp,
            final String apiPort,
            final List<String> statsWhitelist,
            final Map<String, Object> config) {
        final Context context = new Context();
        return new CgMiner.Builder(context, statsWhitelist)
                .setApiIp(apiIp)
                .setApiPort(apiPort)
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
                .setMacStrategy(
                        new HonorKnightMacStrategy(
                                apiIp,
                                Integer.parseInt(
                                        config.getOrDefault(
                                                "testPort",
                                                "80").toString())))
                .build();
    }
}
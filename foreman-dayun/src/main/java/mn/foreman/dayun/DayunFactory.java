package mn.foreman.dayun;

import mn.foreman.cgminer.*;
import mn.foreman.cgminer.request.CgMinerCommand;
import mn.foreman.cgminer.request.CgMinerRequest;
import mn.foreman.dayun.response.StatsPatchingStrategy;
import mn.foreman.dayun.response.StatsResponseStrategy;
import mn.foreman.model.Miner;
import mn.foreman.model.MinerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

/**
 * A {@link MinerFactory} implementation that parses a configuration and creates
 * a {@link Miner} that will query a Dayun miner.
 */
public class DayunFactory
        implements MinerFactory {

    @Override
    public Miner create(final Map<String, String> config) {
        final Context context = new Context();
        final ObjectMapper objectMapper = new ObjectMapper();
        return new CgMiner.Builder()
                .setApiIp(config.get("apiIp"))
                .setApiPort(config.get("apiPort"))
                .addRequest(
                        new CgMinerRequest.Builder()
                                .setCommand(CgMinerCommand.POOLS)
                                .build(),
                        new RawStoringDecorator(
                                context,
                                objectMapper,
                                new PoolsResponseStrategy(
                                        new MrrRigIdCallback(context))))
                .addRequest(
                        new CgMinerRequest.Builder()
                                .setCommand(CgMinerCommand.STATS)
                                .build(),
                        new RawStoringDecorator(
                                context,
                                objectMapper,
                                new StatsResponseStrategy(context)),
                        new StatsPatchingStrategy())
                .build();
    }
}
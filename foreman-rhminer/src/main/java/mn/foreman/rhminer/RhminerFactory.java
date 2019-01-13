package mn.foreman.rhminer;

import mn.foreman.model.Miner;
import mn.foreman.model.MinerFactory;

import java.util.Map;

/**
 * A {@link MinerFactory} implementation that parses a configuration and creates
 * a {@link Miner} that will query an instance of rhminer.
 */
public class RhminerFactory
        implements MinerFactory {

    @Override
    public Miner create(final Map<String, String> config) {
        return new Rhminer(
                config.get("apiIp"),
                Integer.parseInt(config.get("apiPort")));
    }
}
package mn.foreman.multminer;

import mn.foreman.model.Miner;
import mn.foreman.model.MinerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * A {@link MinerFactory} implementation that parses a configuration and creates
 * a {@link Miner} that will query an instance of multminer.
 */
public class MultMinerFactory
        implements MinerFactory {

    @SuppressWarnings("unchecked")
    @Override
    public Miner create(final Map<String, Object> config) {
        return new MultMiner(
                config.get("apiIp").toString(),
                Integer.parseInt(config.get("apiPort").toString()),
                (List<String>) config.getOrDefault(
                        "statsWhitelist",
                        Collections.emptyList()));
    }
}

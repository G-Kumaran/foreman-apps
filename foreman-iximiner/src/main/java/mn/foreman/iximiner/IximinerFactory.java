package mn.foreman.iximiner;

import mn.foreman.model.Miner;
import mn.foreman.model.MinerFactory;

import java.util.Map;

/**
 * A {@link MinerFactory} implementation that parses a configuration and creates
 * a {@link Miner} that will query an instance of t-rex.
 */
public class IximinerFactory
        implements MinerFactory {

    @Override
    public Miner create(final Map<String, String> config) {
        final String apiIp = config.get("apiIp");
        final int apiPort = Integer.parseInt(config.get("apiPort"));
        return new Iximiner(
                apiIp,
                apiPort);
    }
}
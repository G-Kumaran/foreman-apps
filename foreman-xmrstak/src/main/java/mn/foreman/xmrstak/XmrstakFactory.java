package mn.foreman.xmrstak;

import mn.foreman.model.Miner;
import mn.foreman.model.MinerFactory;

import java.util.Map;

/**
 * A {@link MinerFactory} implementation that parses a configuration and creates
 * a {@link Miner} that will query an instance of xmrstak.
 */
public class XmrstakFactory
        implements MinerFactory {

    @Override
    public Miner create(final Map<String, String> config) {
        return new Xmrstak(
                config.get("name"),
                config.get("apiIp"),
                Integer.parseInt(config.get("apiPort")));
    }
}
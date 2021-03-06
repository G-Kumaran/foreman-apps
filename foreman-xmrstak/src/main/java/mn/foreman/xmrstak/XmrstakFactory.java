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

    /** The type. */
    private final XmrstakType type;

    /**
     * Constructor.
     *
     * @param type The type.
     */
    public XmrstakFactory(final XmrstakType type) {
        this.type = type;
    }

    @Override
    public Miner create(final Map<String, Object> config) {
        final Miner miner;

        final String apiIp = config.get("apiIp").toString();
        final int apiPort = Integer.parseInt(config.get("apiPort").toString());

        switch (this.type) {
            case CPU:
                miner =
                        new XmrstakCpu(
                                apiIp,
                                apiPort);
                break;
            case GPU:
                // Fall through
            default:
                miner =
                        new XmrstakGpu(
                                apiIp,
                                apiPort);
                break;
        }

        return miner;
    }
}
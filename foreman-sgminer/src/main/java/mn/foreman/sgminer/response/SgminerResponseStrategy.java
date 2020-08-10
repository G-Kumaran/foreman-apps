package mn.foreman.sgminer.response;

import mn.foreman.cgminer.request.CgMinerCommand;
import mn.foreman.cgminer.response.CgMinerResponse;
import mn.foreman.model.error.MinerException;
import mn.foreman.model.miners.MinerStats;

/**
 * An {@link SgminerResponseStrategy} provides an aggregated response processor
 * that can be used to process all responses expected to come from an sgminer.
 * This allows for stateful processing across requests.
 */
public interface SgminerResponseStrategy {

    /**
     * Processes a {@link CgMinerCommand#DEVS DEVS} response.
     *
     * @param builder  The builder.
     * @param response The response.
     *
     * @throws MinerException on unexpected error.
     */
    void processDevs(
            MinerStats.Builder builder,
            CgMinerResponse response)
            throws MinerException;

    /**
     * Processes a {@link CgMinerCommand#POOLS POOLS} response.
     *
     * @param builder  The builder.
     * @param response The response.
     *
     * @throws MinerException on unexpected error.
     */
    void processPools(
            MinerStats.Builder builder,
            CgMinerResponse response)
            throws MinerException;
}

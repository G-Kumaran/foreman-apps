package mn.foreman.pickaxe.cache;

import mn.foreman.model.Miner;

import java.util.List;

/**
 * A {@link MinerCache} provides an in-memory cache that will store {@link Miner
 * miners}.
 */
public interface MinerCache {

    /**
     * Returns all of the {@link Miner miners} in the cache.
     *
     * @return The {@link Miner miners}.
     */
    List<Miner> getMiners();

    /**
     * Sets the {@link Miner miners} in the cache.
     *
     * @param miners The {@link Miner miners}.
     */
    void setMiners(List<Miner> miners);
}
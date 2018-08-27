package mn.foreman.pickaxe.miners.remote.json;

import mn.foreman.pickaxe.miners.remote.ApiType;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * A {@link MinerConfig} provides a model object representation of a FOREMAN
 * dashboard miner configuration.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MinerConfig {

    /** The API IP. */
    @JsonProperty("apiIp")
    public String apiIp;

    /** The API port. */
    @JsonProperty("apiPort")
    public int apiPort;

    /** The API type. */
    @JsonProperty("apiType")
    public ApiType apiType;

    /** The parameters. */
    @JsonProperty("params")
    public List<Param> params;

    @Override
    public String toString() {
        return String.format(
                "%s [ apiIp=%s, apiPort=%d, apiType=%s, params=[%s] ]",
                getClass().getSimpleName(),
                this.apiIp,
                this.apiPort,
                this.apiType,
                this.params);
    }

    /** A miner configuration parameter. */
    public static class Param {

        /** The key. */
        @JsonProperty("key")
        public String key;

        /** The value. */
        @JsonProperty("value")
        public String value;

        @Override
        public String toString() {
            return String.format("%s [ key=%s, value=%s ]",
                    getClass().getSimpleName(),
                    this.key,
                    this.value);
        }
    }
}

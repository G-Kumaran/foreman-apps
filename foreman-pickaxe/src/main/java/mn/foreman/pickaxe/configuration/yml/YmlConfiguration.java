package mn.foreman.pickaxe.configuration.yml;

import mn.foreman.pickaxe.configuration.CgMinerConfig;
import mn.foreman.pickaxe.configuration.Configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.Validate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class YmlConfiguration
        implements Configuration {

    /** The user's API key. */
    private final String apiKey;

    /** The cgminer configurations. */
    private final List<CgMinerConfig> cgminerConfigs;

    /** The FOREMAN API URL. */
    private final String foremanApiUrl;

    /** How frequently to poll, in seconds. */
    private final int pollFrequencyInSeconds;

    /**
     * Constructor.
     *
     * <p>Note: intentionally hidden (built via JACKSON).</p>
     *
     * @param foremanApiUrl          The FOREMAN API URL.
     * @param apiKey                 The API key.
     * @param cgMinerConfigs         The {@link CgMinerConfig cgminer configs}.
     * @param pollFrequencyInSeconds How frequently to poll, in seconds.
     */
    private YmlConfiguration(
            @JsonProperty("foremanApiUrl") final String foremanApiUrl,
            @JsonProperty("apiKey") final String apiKey,
            @JsonProperty("cgminers") final List<CgMinerConfig> cgMinerConfigs,
            @JsonProperty("pollFrequencyInSeconds") int pollFrequencyInSeconds) {
        Validate.notEmpty(
                foremanApiUrl,
                "foremanApiUrl cannot be empty");
        Validate.notEmpty(
                apiKey,
                "apiKey cannot be empty");
        Validate.notNull(
                cgMinerConfigs,
                "cgminer configuration cannot be null");
        Validate.inclusiveBetween(
                0, Integer.MAX_VALUE, pollFrequencyInSeconds,
                "pollFrequencyInSeconds must be positive");
        this.foremanApiUrl = foremanApiUrl;
        this.apiKey = apiKey;
        this.cgminerConfigs = new ArrayList<>(cgMinerConfigs);
        this.pollFrequencyInSeconds = pollFrequencyInSeconds;
    }

    @Override
    public String getApiKey() {
        return this.apiKey;
    }

    @Override
    public List<CgMinerConfig> getCgminerConfigs() {
        return Collections.unmodifiableList(this.cgminerConfigs);
    }

    @Override
    public String getForemanApiUrl() {
        return this.foremanApiUrl;
    }

    @Override
    public int getPollFrequencyInSeconds() {
        return this.pollFrequencyInSeconds;
    }
}
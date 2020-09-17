package mn.foreman.model.miners.asic;

import mn.foreman.model.AbstractBuilder;
import mn.foreman.model.miners.BigDecimalSerializer;
import mn.foreman.model.miners.FanInfo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.math.BigDecimal;
import java.util.*;

/**
 * The following POJO represents a JSON object with the following format:
 *
 * <pre>
 *   {
 *     "hashRate": 13674000000000.52,
 *     "fans": {
 *       "num": 2,
 *       "speeds": [
 *         4080,
 *         4560
 *       ]
 *     },
 *     "temps": [
 *       56,
 *       52,
 *       54,
 *       71,
 *       67,
 *       69
 *     ],
 *     "powerState": "",
 *     "hasErrors": false
 *   }
 * </pre>
 */
public class Asic {

    /** Miscellaneous rig attributes. */
    private final List<Map<String, String>> attributes;

    /** The fan readings. */
    private final FanInfo fans;

    /** Whether or not errors were reported. */
    private final Boolean hasErrors;

    /** The hash rate. */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private final BigDecimal hashRate;

    /** The power state. */
    private final String powerState;

    /** The temp sensor readings. */
    private final List<Integer> temps;

    /**
     * Constructor.
     *
     * @param hashRate   The hash rate.
     * @param fans       The fan information.
     * @param temps      The temperatures.
     * @param powerState The power state.
     * @param hasErrors  Whether or not errors were observed.
     * @param attributes Rig attributes.
     */
    private Asic(
            @JsonProperty("hashRate") final BigDecimal hashRate,
            @JsonProperty("fans") final FanInfo fans,
            @JsonProperty("temps") final List<Integer> temps,
            @JsonProperty("powerState") final String powerState,
            @JsonProperty("hasErrors") final Boolean hasErrors,
            @JsonProperty("attributes") final List<Map<String, String>> attributes) {
        Validate.notNull(
                hashRate,
                "hashRate cannot be null");
        Validate.notNull(
                fans,
                "fans cannot be null");
        Validate.notNull(
                temps,
                "temps cannot be null");
        Validate.notNull(
                hasErrors,
                "hasErrors cannot be null");
        this.hashRate = hashRate;
        this.fans = fans;
        this.temps = new ArrayList<>(temps);
        this.powerState = powerState;
        this.hasErrors = hasErrors;
        this.attributes = new ArrayList<>(attributes);
    }

    @Override
    public boolean equals(final Object other) {
        final boolean isEqual;
        if (other == null) {
            isEqual = false;
        } else if (getClass() != other.getClass()) {
            isEqual = false;
        } else {
            final Asic asic = (Asic) other;
            isEqual =
                    new EqualsBuilder()
                            .append(this.hashRate, asic.hashRate)
                            .append(this.fans, asic.fans)
                            .append(this.temps, asic.temps)
                            .append(this.powerState, asic.powerState)
                            .append(this.hasErrors, asic.hasErrors)
                            .append(this.attributes, asic.attributes)
                            .isEquals();
        }
        return isEqual;
    }

    /**
     * Returns the attributes.
     *
     * @return The attributes.
     */
    public List<Map<String, String>> getAttributes() {
        return Collections.unmodifiableList(this.attributes);
    }

    /**
     * Returns the {@link FanInfo}.
     *
     * @return The {@link FanInfo}.
     */
    public FanInfo getFans() {
        return this.fans;
    }

    /**
     * Returns whether or not errors were observed.
     *
     * @return Whether or not errors were observed.
     */
    public Boolean getHasErrors() {
        return this.hasErrors;
    }

    /**
     * Returns the hash rate.
     *
     * @return The hash rate.
     */
    public BigDecimal getHashRate() {
        return this.hashRate;
    }

    /**
     * Returns the power state.
     *
     * @return The power state.
     */
    public String getPowerState() {
        return this.powerState;
    }

    /**
     * Returns the temps.
     *
     * @return The temps.
     */
    public List<Integer> getTemps() {
        return Collections.unmodifiableList(this.temps);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(this.hashRate)
                .append(this.fans)
                .append(this.temps)
                .append(this.powerState)
                .append(this.hasErrors)
                .append(this.attributes)
                .hashCode();
    }

    @Override
    public String toString() {
        return String.format(
                "%s [ " +
                        "hashRate=%s, " +
                        "fans=%s, " +
                        "temps=%s, " +
                        "powerState=%s, " +
                        "hasErrors=%s, " +
                        "attributes=%s" +
                        " ]",
                getClass().getSimpleName(),
                this.hashRate,
                this.fans,
                this.temps,
                this.powerState,
                this.hasErrors,
                this.attributes);
    }

    /** A builder for creating {@link Asic ASICs}. */
    public static class Builder
            extends AbstractBuilder<Asic> {

        /** The attributes. */
        private final List<Map<String, String>> attributes = new ArrayList<>();

        /** The temperatures. */
        private final List<Integer> temps = new LinkedList<>();

        /** The fan information. */
        private FanInfo fanInfo;

        /** Whether or not errors were observed. */
        private Boolean hasErrors = UNDEFINED_BOOL;

        /** The hash rate. */
        private BigDecimal hashRate;

        /** The power state. */
        private String powerState = null;

        /**
         * Adds a rig attribute.
         *
         * @param key   The key.
         * @param value The value.
         *
         * @return This builder instance.
         */
        public Asic.Builder addAttribute(
                final String key,
                final String value) {
            if ((key != null) && (!key.isEmpty()) &&
                    (value != null) && (!value.isEmpty())) {
                this.attributes.add(
                        ImmutableMap.of(
                                "key",
                                key,
                                "value",
                                value));
            }
            return this;
        }

        /**
         * Adds the provided attributes.
         *
         * @param attributes The attributes to add.
         *
         * @return This builder instance.
         */
        public Asic.Builder addAttributes(
                final List<Map<String, String>> attributes) {
            if (attributes != null) {
                attributes.forEach(this::addAttributes);
            }
            return this;
        }

        /**
         * Adds the provided attributes.
         *
         * @param attributes The attributes to add.
         *
         * @return This builder instance.
         */
        public Asic.Builder addAttributes(final Map<String, String> attributes) {
            if (attributes != null) {
                this.attributes.add(new HashMap<>(attributes));
            }
            return this;
        }

        /**
         * Adds a new temperature reading.
         *
         * @param temp The new temperature reading.
         *
         * @return The builder instance.
         */
        public Builder addTemp(final String temp) {
            if ((temp != null) && !temp.isEmpty()) {
                return addTemp(Double.valueOf(temp).intValue());
            }
            return this;
        }

        /**
         * Adds a new temperature reading.
         *
         * @param temp The new temperature reading.
         *
         * @return The builder instance.
         */
        public Builder addTemp(final int temp) {
            // Ignore temps of 0, as many miners report metrics for sensors
            // that aren't being used.  If there's a problem, no sensors
            // will display a temperature and an error will display
            if (temp != 0) {
                this.temps.add(temp);
            }
            return this;
        }

        @Override
        public Asic build() {
            return new Asic(
                    this.hashRate,
                    this.fanInfo,
                    this.temps,
                    this.powerState,
                    this.hasErrors,
                    this.attributes);
        }

        /**
         * Creates a builder from the asic provided.
         *
         * @param asic The asic to reference.
         *
         * @return This builder instance.
         */
        public Builder fromAsic(final Asic asic) {
            if (asic != null) {
                setFanInfo(asic.fans);
                hasErrors(asic.hasErrors);
                setHashRate(asic.hashRate);
                setPowerState(asic.powerState);
                asic.temps.forEach(this::addTemp);
                addAttributes(asic.attributes);
            }
            return this;
        }

        /**
         * Sets whether or not errors were observed.
         *
         * @param hasErrors Whether or not errors were observed.
         *
         * @return The builder instance.
         */
        public Builder hasErrors(final Boolean hasErrors) {
            this.hasErrors = hasErrors;
            return this;
        }

        /**
         * Sets the fan information.
         *
         * @param fanInfo The fan info.
         *
         * @return The builder instance.
         */
        public Builder setFanInfo(final FanInfo fanInfo) {
            this.fanInfo = fanInfo;
            return this;
        }

        /**
         * Sets the hash rate.
         *
         * @param hashRate The hash rate.
         *
         * @return The builder instance.
         */
        public Builder setHashRate(final BigDecimal hashRate) {
            this.hashRate = hashRate;
            return this;
        }

        /**
         * Sets the rig id
         *
         * @param rigId The rig id.
         *
         * @return This builder instance.
         */
        public Asic.Builder setMrrRigId(final String rigId) {
            if (rigId != null && !rigId.isEmpty()) {
                addAttribute(
                        "mrr_rig_id",
                        rigId);
            }
            return this;
        }

        /**
         * Sets the power state.
         *
         * @param powerState The power state.
         *
         * @return This builder instance.
         */
        public Builder setPowerState(final String powerState) {
            if (powerState != null) {
                this.powerState = powerState.toLowerCase();
            }
            return this;
        }
    }
}
package mn.foreman.lolminer.v6.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.List;

/**
 * The following model provides a {@link Response} model object of a response
 * from lolminer.
 *
 * <pre>
 * {
 *   "Software": "lolMiner 0.6",
 *   "Mining": {
 *     "Coin": "AION",
 *     "Algorithm": "Equihash 210\/9"
 *   },
 *   "Stratum": {
 *     "Current_Pool": "na.aionmine.org:3333",
 *     "Current_User": "0xa0e1ff18f69eac5d17fc8c5ac078739d64cc0a8ae2f84b7ca6d55c33bff8dc52.lolMiner",
 *     "Average_Latency": 0.0
 *   },
 *   "Session": {
 *     "Startup": 1547410837,
 *     "Startup_String": "2019-01-13_15-20-37",
 *     "Uptime": 140,
 *     "Last_Update": 1547410977,
 *     "Active_GPUs": 1,
 *     "Performance_Summary": 65.1,
 *     "Accepted": 4,
 *     "Submitted": 4
 *   },
 *   "GPUs": [
 *     {
 *       "Index": 0,
 *       "Name": "GeForce GTX 1050 Ti",
 *       "Performance": 65.1,
 *       "Session_Accepted": 4,
 *       "Session_Submitted": 4,
 *       "PCIE_Address": "1:0"
 *     }
 *   ]
 * }
 * </pre>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response {

    /** The GPUs. */
    @JsonProperty("GPUs")
    public List<Gpu> gpus;

    /** The session. */
    @JsonProperty("Session")
    public Session session;

    /** The stratum. */
    @JsonProperty("Stratum")
    public Stratum stratum;

    /** A model representation of a GPU. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Gpu {

        /** The address. */
        @JsonProperty("PCIE_Address")
        public String address;

        /** The index. */
        @JsonProperty("Index")
        public int index;

        /** The name. */
        @JsonProperty("Name")
        public String name;
    }

    /** A model representation of a session. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Session {

        /** The total number of accepted shares. */
        @JsonProperty("Accepted")
        public int accepted;

        /** The current hash rate. */
        @JsonProperty("Performance_Summary")
        public BigDecimal hashRate;

        /** The total number of submitted shares. */
        @JsonProperty("Submitted")
        public int submitted;

        /** The connection uptime. */
        @JsonProperty("Uptime")
        public int uptime;
    }

    /** A model representation of the stratum. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Stratum {

        /** The current pool. */
        @JsonProperty("Current_Pool")
        public String pool;
    }
}

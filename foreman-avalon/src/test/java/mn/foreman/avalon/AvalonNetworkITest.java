package mn.foreman.avalon;

import mn.foreman.util.AbstractAsyncActionITest;
import mn.foreman.util.TestUtils;
import mn.foreman.util.http.FakeHttpMinerServer;
import mn.foreman.util.http.HttpHandler;
import mn.foreman.util.http.ServerHandler;
import mn.foreman.util.rpc.FakeRpcMinerServer;
import mn.foreman.util.rpc.HandlerInterface;
import mn.foreman.util.rpc.RpcHandler;

import com.google.common.collect.ImmutableMap;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

/** Tests changing networks on an Avalon miner. */
@RunWith(Parameterized.class)
public class AvalonNetworkITest
        extends AbstractAsyncActionITest {

    /**
     * Constructor.
     *
     * @param httpsHandlers The HTTP handlers.
     * @param rpcHandlers   The RPC handlers.
     */
    public AvalonNetworkITest(
            final Map<String, ServerHandler> httpsHandlers,
            final Map<String, HandlerInterface> rpcHandlers) {
        super(
                8080,
                4029,
                new AvalonNetworkAction(new AvalonRebootAction()),
                Arrays.asList(
                        () -> new FakeHttpMinerServer(
                                8080,
                                httpsHandlers),
                        () -> new FakeRpcMinerServer(
                                4029,
                                rpcHandlers)),
                new AvalonFactory(),
                TestUtils.toNetworkJson(
                        4029,
                        false),
                true);
    }

    /**
     * Test parameters
     *
     * @return The test parameters.
     */
    @Parameterized.Parameters
    public static Collection<Object[]> parameters() {
        return Arrays.asList(
                new Object[][]{
                        {
                                // Avalon 1047
                                ImmutableMap.of(
                                        "/network.cgi",
                                        new HttpHandler(
                                                "protocol=1&ip=192.168.1.189&mask=255.255.255.0&gateway=192.168.1.1&dns=192.168.1.1&dnsbak=192.168.1.1",
                                                ""),
                                        "/reboot.cgi",
                                        new HttpHandler(
                                                "reboot=1",
                                                "")),
                                ImmutableMap.of(
                                        "{\"command\":\"pools+summary+stats\"}",
                                        new RpcHandler(
                                                "{\"summary\":[{\"STATUS\":[{\"STATUS\":\"S\",\"When\":168,\"Code\":11,\"Msg\":\"Summary\",\"Description\":\"cgminer 4.11.1\"}],\"SUMMARY\":[{\"Elapsed\":155,\"MHS av\":25536751.74,\"MHS 30s\":32247416.81,\"MHS 1m\":28134393.97,\"MHS 5m\":10769774.69,\"MHS 15m\":4090974.03,\"Found Blocks\":0,\"Getworks\":13,\"Accepted\":6,\"Rejected\":0,\"Hardware Errors\":0,\"Utility\":2.32,\"Discarded\":3691802,\"Stale\":0,\"Get Failures\":0,\"Local Work\":1800,\"Remote Failures\":0,\"Network Blocks\":4,\"Total MH\":3940649502.0000,\"Work Utility\":354447.38,\"Difficulty Accepted\":794291.61511800,\"Difficulty Rejected\":0.00000000,\"Difficulty Stale\":0.00000000,\"Best Share\":222408,\"Device Hardware%\":0.0000,\"Device Rejected%\":0.0000,\"Pool Rejected%\":0.0000,\"Pool Stale%\":0.0000,\"Last getwork\":0}],\"id\":1}],\"stats\":[{\"STATUS\":[{\"STATUS\":\"S\",\"When\":168,\"Code\":70,\"Msg\":\"CGMiner stats\",\"Description\":\"cgminer 4.11.1\"}],\"STATS\":[{\"STATS\":0,\"ID\":\"AVA100\",\"Elapsed\":155,\"Calls\":0,\"Wait\":0.000000,\"Max\":0.000000,\"Min\":99999999.000000,\"MM ID0\":\"Ver[1047-20051101_85dc6f0_0332654] DNA[02010000cb231da8] MEMFREE[1413136.0] NETFAIL[0 0 0 0 0 0 0 0] SYSTEMSTATU[Work: In Work, Hash Board: 2 ] Elapsed[157] BOOTBY[0x0C.00000001] LW[37060] MH[0 0] HW[0] DH[6.118%] Temp[18] TMax[64] TAvg[57] Fan1[2896] Fan2[2927] FanR[31%] Vo[0] PS[0 0 0 0 0 0] PLL0[1925 1138 1411 2606] PLL1[1359 1377 1905 2439] GHSmm[37470.98] GHSavg[25174.76] WU[351687.39] Freq[661.56] Led[0] MGHS[12362.61 12812.16] MTmax[61 64] MTavg[55 58] TA[240] PING[100] ECHU[512 512] ECMM[0] SF0[612 637 662 687] SF1[637 662 687 712] PVT_T0[ 56  56  54  54  56  56  56  55  54  54  56  56  56  56  55  55  56  57  58  55  55  56  59  58  59  57  56  55  59  58  58  58  56  54  58  58  58  57  55  53  57  56  55  55  53  51  55  53  53  53  51  48  53  51  52  53  50  47  51  50  52  52  52  53  54  53  54  55  55  54  55  54  55  56  57  57  57  56  57  59  61  59  59  58  57  59  59  59  60  58  58  60  60  59  60  58  58  59  60  57  59  57  56  58  57  57  57  56  56  56  58  56  58  56  56  57  56  54  56  56] PVT_T1[ 58  59  56  56  60  59  58  58  58  56  58  59  58  58  56  56  59  58  59  59  59  59  61  60  61  61  60  59  61  62  62  61  59  58  61  62  62  59  58  56  59  61  59  58  55  52  56  56  56  55  53  52  54  55  55  54  52  51  53  54  54  56  56  58  58  56  57  59  60  59  59  58  57  59  59  60  60  58  61  63  61  61  63  62  62  62  62  63  63  62  62  62  62  62  64  62  60  62  61  59  62  60  58  62  60  59  61  58  59  60  60  59  61  59  60  62  59  57  59  60] PVT_V0[319 321 322 322 322 322 318 318 320 318 319 319 322 322 321 319 317 318 319 321 319 316 315 317 314 317 319 315 315 317 317 316 317 318 317 317 318 317 319 316 315 317 323 321 322 322 319 319 320 320 320 322 321 322 325 324 325 323 324 327 327 325 324 324 324 326 325 325 324 321 320 318 322 320 320 319 320 319 315 316 315 317 320 320 320 318 320 319 319 319 318 317 316 314 313 314 319 318 317 324 323 324 318 319 320 323 324 323 323 322 321 322 323 323 320 320 320 322 323 322] PVT_V1[317 320 324 317 317 319 323 323 322 329 331 329 317 318 318 315 316 317 332 332 331 323 325 323 318 318 320 319 319 319 315 315 315 314 311 311 319 319 318 317 322 320 319 321 327 317 318 320 322 321 322 319 319 319 322 321 324 322 325 327 327 324 322 319 319 321 320 321 321 319 318 318 325 321 320 323 320 320 315 317 315 317 317 319 311 311 313 311 313 313 317 317 316 318 316 316 318 316 316 323 321 322 318 318 318 318 319 322 322 323 323 321 321 321 325 323 325 319 318 316] MW[18785 18800] MW0[1 3 4 0 3 4 4 3 1 4 3 6 10 0 5 1 3 1 5 3 3 1 5 3 2 2 2 4 2 2 3 2 4 9 1 5 3 4 2 5 6 6 4 4 6 7 2 2 4 2 3 3 3 4 5 5 5 4 7 1 3 6 5 5 4 4 2 2 0 3 1 3 1 3 6 4 2 0 10 2 5 5 2 2 5 2 6 2 8 6 6 3 8 6 7 6 3 3 7 3 6 4 4 4 2 3 4 0 3 1 4 0 4 5 5 5 5 2 3 4] MW1[5 4 2 4 3 2 1 3 6 4 5 3 3 3 2 4 7 3 4 5 3 4 4 2 3 6 6 2 3 3 4 4 3 4 1 2 7 1 5 5 4 2 5 1 4 3 4 6 3 4 8 3 3 2 5 3 2 2 5 3 1 8 2 5 7 2 4 3 7 2 4 1 6 2 4 3 4 4 4 1 1 4 7 2 3 1 3 5 8 2 3 3 8 2 6 5 8 1 7 3 3 8 5 2 3 4 7 1 9 2 3 2 4 10 3 3 5 3 2 4] CRC[1 0] POW_I2C[FAILED] FACOPTS0[] FACOPTS1[] ATAOPTS0[--avalon10-freq 562:587:612:637 --avalon10-voltage-level 36 ] ATAOPTS1[--avalon10-freq 637:662:687:712 --avalon10-voltage-level 56 ] ADJ[1] MPO[2220] MVL[71] ATABD0[612 637 662 687] ATABD1[637 662 687 712] WORKMODE[1]\",\"MM Count\":1,\"Smart Speed\":1,\"Voltage Level Offset\":0,\"Nonce Mask\":25},{\"STATS\":1,\"ID\":\"POOL0\",\"Elapsed\":155,\"Calls\":0,\"Wait\":0.000000,\"Max\":0.000000,\"Min\":99999999.000000,\"Pool Calls\":0,\"Pool Attempts\":0,\"Pool Wait\":0.000000,\"Pool Max\":0.000000,\"Pool Min\":99999999.000000,\"Pool Av\":0.000000,\"Work Had Roll Time\":false,\"Work Can Roll\":false,\"Work Had Expire\":false,\"Work Roll Time\":0,\"Work Diff\":132381.93585300,\"Min Diff\":132381.93585300,\"Max Diff\":132381.93585300,\"Min Diff Count\":890,\"Max Diff Count\":890,\"Times Sent\":8,\"Bytes Sent\":1014,\"Times Recv\":26,\"Bytes Recv\":13164,\"Net Bytes Sent\":1014,\"Net Bytes Recv\":13164},{\"STATS\":2,\"ID\":\"POOL1\",\"Elapsed\":155,\"Calls\":0,\"Wait\":0.000000,\"Max\":0.000000,\"Min\":99999999.000000,\"Pool Calls\":0,\"Pool Attempts\":0,\"Pool Wait\":0.000000,\"Pool Max\":0.000000,\"Pool Min\":99999999.000000,\"Pool Av\":0.000000,\"Work Had Roll Time\":false,\"Work Can Roll\":false,\"Work Had Expire\":false,\"Work Roll Time\":0,\"Work Diff\":0.00000000,\"Min Diff\":0.00000000,\"Max Diff\":0.00000000,\"Min Diff Count\":0,\"Max Diff Count\":0,\"Times Sent\":4,\"Bytes Sent\":332,\"Times Recv\":5,\"Bytes Recv\":1553,\"Net Bytes Sent\":332,\"Net Bytes Recv\":1553},{\"STATS\":3,\"ID\":\"POOL2\",\"Elapsed\":155,\"Calls\":0,\"Wait\":0.000000,\"Max\":0.000000,\"Min\":99999999.000000,\"Pool Calls\":0,\"Pool Attempts\":0,\"Pool Wait\":0.000000,\"Pool Max\":0.000000,\"Pool Min\":99999999.000000,\"Pool Av\":0.000000,\"Work Had Roll Time\":false,\"Work Can Roll\":false,\"Work Had Expire\":false,\"Work Roll Time\":0,\"Work Diff\":132381.93585300,\"Min Diff\":132381.93585300,\"Max Diff\":132381.93585300,\"Min Diff Count\":2,\"Max Diff Count\":2,\"Times Sent\":4,\"Bytes Sent\":354,\"Times Recv\":11,\"Bytes Recv\":3704,\"Net Bytes Sent\":354,\"Net Bytes Recv\":3704}],\"id\":1}],\"pools\":[{\"STATUS\":[{\"STATUS\":\"S\",\"When\":168,\"Code\":7,\"Msg\":\"3 Pool(s)\",\"Description\":\"cgminer 4.11.1\"}],\"POOLS\":[{\"POOL\":0,\"URL\":\"stratum+tcp://sha256.mining-dutch.nl:9996\",\"Status\":\"Alive\",\"Priority\":0,\"Quota\":1,\"Long Poll\":\"N\",\"Getworks\":10,\"Accepted\":5,\"Rejected\":0,\"Works\":896,\"Discarded\":0,\"Stale\":0,\"Get Failures\":0,\"Remote Failures\":0,\"User\":\"xxx\",\"Last Share Time\":98,\"Diff1 Shares\":917504,\"Proxy Type\":\"\",\"Proxy\":\"\",\"Difficulty Accepted\":661909.67926500,\"Difficulty Rejected\":0.00000000,\"Difficulty Stale\":0.00000000,\"Last Share Difficulty\":132381.93585300,\"Work Difficulty\":132381.93585300,\"Has Stratum\":true,\"Stratum Active\":true,\"Stratum URL\":\"sha256asicboost.eu.nicehash.com\",\"Stratum Difficulty\":132381.93585300,\"Has Vmask\":true,\"Has GBT\":false,\"Best Share\":222408,\"Pool Rejected%\":0.0000,\"Pool Stale%\":0.0000,\"Bad Work\":3,\"Current Block Height\":654933,\"Current Block Version\":536870912},{\"POOL\":1,\"URL\":\"stratum+tcp://sha256.antpool.com:3333\",\"Status\":\"Alive\",\"Priority\":1,\"Quota\":1,\"Long Poll\":\"N\",\"Getworks\":1,\"Accepted\":0,\"Rejected\":0,\"Works\":0,\"Discarded\":0,\"Stale\":0,\"Get Failures\":0,\"Remote Failures\":0,\"User\":\"xxx\",\"Last Share Time\":0,\"Diff1 Shares\":0,\"Proxy Type\":\"\",\"Proxy\":\"\",\"Difficulty Accepted\":0.00000000,\"Difficulty Rejected\":0.00000000,\"Difficulty Stale\":0.00000000,\"Last Share Difficulty\":0.00000000,\"Work Difficulty\":0.00000000,\"Has Stratum\":true,\"Stratum Active\":false,\"Stratum URL\":\"\",\"Stratum Difficulty\":0.00000000,\"Has Vmask\":true,\"Has GBT\":false,\"Best Share\":0,\"Pool Rejected%\":0.0000,\"Pool Stale%\":0.0000,\"Bad Work\":0,\"Current Block Height\":0,\"Current Block Version\":536870912},{\"POOL\":2,\"URL\":\"stratum+tcp://sha256.mining-dutch.nl:9996\",\"Status\":\"Alive\",\"Priority\":2,\"Quota\":1,\"Long Poll\":\"N\",\"Getworks\":2,\"Accepted\":1,\"Rejected\":0,\"Works\":0,\"Discarded\":0,\"Stale\":0,\"Get Failures\":0,\"Remote Failures\":0,\"User\":\"xxx\",\"Last Share Time\":0,\"Diff1 Shares\":0,\"Proxy Type\":\"\",\"Proxy\":\"\",\"Difficulty Accepted\":132381.93585300,\"Difficulty Rejected\":0.00000000,\"Difficulty Stale\":0.00000000,\"Last Share Difficulty\":0.00000000,\"Work Difficulty\":132381.93585300,\"Has Stratum\":true,\"Stratum Active\":false,\"Stratum URL\":\"\",\"Stratum Difficulty\":0.00000000,\"Has Vmask\":true,\"Has GBT\":false,\"Best Share\":0,\"Pool Rejected%\":0.0000,\"Pool Stale%\":0.0000,\"Bad Work\":0,\"Current Block Height\":650502,\"Current Block Version\":536870912}],\"id\":1}],\"id\":1}"))
                        }
                });
    }
}
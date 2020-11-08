package mn.foreman.whatsminer;

import mn.foreman.cgminer.CgMinerDetectionStrategy;
import mn.foreman.cgminer.request.CgMinerCommand;
import mn.foreman.model.Detection;
import mn.foreman.util.AbstractDetectITest;
import mn.foreman.util.FakeMinerServer;
import mn.foreman.util.http.FakeHttpMinerServer;
import mn.foreman.util.http.HttpHandler;
import mn.foreman.util.rpc.FakeRpcMinerServer;
import mn.foreman.util.rpc.RpcHandler;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.io.IOUtils;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/** Tests detection of whatsminer miners. */
@RunWith(Parameterized.class)
public class WhatsminerDetectITest
        extends AbstractDetectITest {

    /**
     * Constructor.
     *
     * @param fakeServers  The fake servers.
     * @param expectedType The expected type.
     */
    public WhatsminerDetectITest(
            final List<Supplier<FakeMinerServer>> fakeServers,
            final WhatsminerType expectedType) {
        super(
                new FirmwareAwareDetectionStrategy(
                        new CgMinerDetectionStrategy(
                                CgMinerCommand.STATS,
                                new WhatsminerTypeFactory()),
                        new WhatsminerDetectionStrategy()),
                fakeServers,
                toArgs(),
                Detection.builder()
                        .minerType(expectedType)
                        .ipAddress("127.0.0.1")
                        .port(4028)
                        .parameters(toArgs())
                        .build(),
                (integer, stringObjectMap) -> {
                    stringObjectMap.put(
                            "webPort",
                            (integer != 4028
                                    ? "8081"
                                    : "8080"));
                    return stringObjectMap;
                });
    }

    /**
     * Test parameters
     *
     * @return The test parameters.
     */
    @Parameterized.Parameters
    public static List<Object[]> parameters() {
        return Arrays.asList(
                new Object[][]{
                        {
                                // Whatsminer M3
                                Collections.singletonList(
                                        (Supplier<FakeMinerServer>) () -> {
                                            try {
                                                return new FakeRpcMinerServer(
                                                        4028,
                                                        ImmutableMap.of(
                                                                "{\"command\":\"stats\"}",
                                                                new RpcHandler(
                                                                        IOUtils.toString(
                                                                                WhatsminerDetectITest.class.getResourceAsStream(
                                                                                        "/m3.stats.json"),
                                                                                Charset.defaultCharset()))));
                                            } catch (final IOException e) {
                                                throw new AssertionError(e);
                                            }
                                        }),
                                WhatsminerType.WHATSMINER_M3
                        },
                        {
                                // Whatsminer M20S (old firmware)
                                Collections.singletonList(
                                        (Supplier<FakeMinerServer>) () -> {
                                            try {
                                                return new FakeRpcMinerServer(
                                                        4028,
                                                        ImmutableMap.of(
                                                                "{\"command\":\"stats\"}",
                                                                new RpcHandler(
                                                                        IOUtils.toString(
                                                                                WhatsminerDetectITest.class.getResourceAsStream(
                                                                                        "/m20s.stats.json"),
                                                                                Charset.defaultCharset()))));
                                            } catch (final Exception e) {
                                                throw new AssertionError(e);
                                            }
                                        }),
                                WhatsminerType.WHATSMINER_M20S
                        },
                        {
                                // Whatsminer M20S (new firmware)
                                Arrays.asList(
                                        () ->
                                                new FakeRpcMinerServer(
                                                        4028,
                                                        ImmutableMap.of(
                                                                "{\"command\":\"stats\"}",
                                                                new RpcHandler(
                                                                        "{\"STATUS\":\"E\",\"When\":1604756008,\"Code\":14,\"Msg\":\"invalid cmd\",\"Description\":\"whatsminer v1.1\"}"))),
                                        (Supplier<FakeMinerServer>) () ->
                                                new FakeHttpMinerServer(
                                                        8080,
                                                        ImmutableMap.of(
                                                                "/cgi-bin/luci/",
                                                                new HttpHandler(
                                                                        "luci_username=username&luci_password=password",
                                                                        Collections.emptyMap(),
                                                                        "",
                                                                        ImmutableMap.of(
                                                                                "Set-Cookie",
                                                                                "sysauth=c57ede0698febf098dca307d106376d0")),
                                                                "/cgi-bin/luci/admin/status/overview",
                                                                new HttpHandler(
                                                                        "",
                                                                        ImmutableMap.of(
                                                                                "Cookie",
                                                                                "sysauth=c57ede0698febf098dca307d106376d0"),
                                                                        "<!DOCTYPE html>\n" +
                                                                                "<html lang=\"en\">\n" +
                                                                                "\t<head>\n" +
                                                                                "\t\t<meta charset=\"utf-8\">\n" +
                                                                                "\t\t<title>WhatsMiner - Overview - LuCI</title>\n" +
                                                                                "\t\t<!--[if lt IE 9]><script src=\"/luci-static/bootstrap/html5.js?v=git-16.336.70424-1fd43b4\"></script><![endif]-->\n" +
                                                                                "\t\t<meta name=\"viewport\" content=\"initial-scale=1.0\">\n" +
                                                                                "\t\t<link rel=\"stylesheet\" href=\"/luci-static/bootstrap/cascade.css?v=git-16.336.70424-1fd43b4\">\n" +
                                                                                "\t\t<link rel=\"stylesheet\" media=\"only screen and (max-device-width: 854px)\" href=\"/luci-static/bootstrap/mobile.css?v=git-16.336.70424-1fd43b4\" type=\"text/css\" />\n" +
                                                                                "\t\t<link rel=\"shortcut icon\" href=\"/luci-static/bootstrap/favicon.ico\">\n" +
                                                                                "\t\t<script src=\"/luci-static/resources/xhr.js?v=git-16.336.70424-1fd43b4\"></script>\n" +
                                                                                "\t</head>\n" +
                                                                                "\n" +
                                                                                "\t<body class=\"lang_enOverview\">\n" +
                                                                                "\t\t<header>\n" +
                                                                                "\t\t\t<div class=\"fill\">\n" +
                                                                                "\t\t\t\t<div class=\"container\">\n" +
                                                                                "\t\t\t\t\t<a class=\"brand\" href=\"#\">WhatsMiner</a>\n" +
                                                                                "\t\t\t\t\t<ul class=\"nav\"><li class=\"dropdown\"><a class=\"menu\" href=\"#\">Status</a><ul class=\"dropdown-menu\"><li><a href=\"/cgi-bin/luci/admin/status/cgminerstatus\">CGMiner Status</a></li><li><a href=\"/cgi-bin/luci/admin/status/cgminerapi\">CGMiner API Log</a></li><li><a href=\"/cgi-bin/luci/admin/status/syslog\">System Log</a></li><li><a href=\"/cgi-bin/luci/admin/status/minerlog\">Miner Log</a></li><li><a href=\"/cgi-bin/luci/admin/status/processes\">Processes</a></li><li><a href=\"/cgi-bin/luci/admin/status/overview\">Overview</a></li></ul></li><li class=\"dropdown\"><a class=\"menu\" href=\"#\">System</a><ul class=\"dropdown-menu\"><li><a href=\"/cgi-bin/luci/admin/system/system\">System</a></li><li><a href=\"/cgi-bin/luci/admin/system/admin\">Administration</a></li><li><a href=\"/cgi-bin/luci/admin/system/reboot\">Reboot</a></li></ul></li><li class=\"dropdown\"><a class=\"menu\" href=\"#\">Configuration</a><ul class=\"dropdown-menu\"><li><a href=\"/cgi-bin/luci/admin/network/network\">Interfaces</a></li><li><a href=\"/cgi-bin/luci/admin/network/cgminer\">CGMiner Configuration</a></li></ul></li><li><a href=\"/cgi-bin/luci/admin/logout\">Logout</a></li></ul>\n" +
                                                                                "\t\t\t\t\t<div class=\"pull-right\">\n" +
                                                                                "\t\t\t\t\t\t\n" +
                                                                                "\t\t\t\t\t\t<span id=\"xhr_poll_status\" style=\"display:none\" onclick=\"XHR.running() ? XHR.halt() : XHR.run()\">\n" +
                                                                                "\t\t\t\t\t\t\t<span class=\"label success\" id=\"xhr_poll_status_on\">Auto Refresh on</span>\n" +
                                                                                "\t\t\t\t\t\t\t<span class=\"label\" id=\"xhr_poll_status_off\" style=\"display:none\">Auto Refresh off</span>\n" +
                                                                                "\t\t\t\t\t\t</span>\n" +
                                                                                "\t\t\t\t\t</div>\n" +
                                                                                "\t\t\t\t</div>\n" +
                                                                                "\t\t\t</div>\n" +
                                                                                "\t\t</header><div id=\"maincontent\" class=\"container\">\n" +
                                                                                "\t\t\t\n" +
                                                                                "\n" +
                                                                                "\n" +
                                                                                "\n" +
                                                                                "<script type=\"text/javascript\" src=\"/luci-static/resources/cbi.js?v=git-16.336.70424-1fd43b4\"></script>\n" +
                                                                                "<script type=\"text/javascript\">//<![CDATA[\n" +
                                                                                "\tfunction progressbar(v, m)\n" +
                                                                                "\t{\n" +
                                                                                "\t\tvar vn = parseInt(v) || 0;\n" +
                                                                                "\t\tvar mn = parseInt(m) || 100;\n" +
                                                                                "\t\tvar pc = Math.floor((100 / mn) * vn);\n" +
                                                                                "\n" +
                                                                                "\t\treturn String.format(\n" +
                                                                                "\t\t\t'<div style=\"width:200px; position:relative; border:1px solid #999999\">' +\n" +
                                                                                "\t\t\t\t'<div style=\"background-color:#CCCCCC; width:%d%%; height:15px\">' +\n" +
                                                                                "\t\t\t\t\t'<div style=\"position:absolute; left:0; top:0; text-align:center; width:100%%; color:#000000\">' +\n" +
                                                                                "\t\t\t\t\t\t'<small>%s / %s (%d%%)</small>' +\n" +
                                                                                "\t\t\t\t\t'</div>' +\n" +
                                                                                "\t\t\t\t'</div>' +\n" +
                                                                                "\t\t\t'</div>', pc, v, m, pc\n" +
                                                                                "\t\t);\n" +
                                                                                "\t}\n" +
                                                                                "\n" +
                                                                                "\tfunction wifirate(bss, rx) {\n" +
                                                                                "\t\tvar p = rx ? 'rx_' : 'tx_',\n" +
                                                                                "\t\t    s = '%.1f Mbit/s, %dMHz'\n" +
                                                                                "\t\t\t\t\t.format(bss[p+'rate'] / 1000, bss[p+'mhz']),\n" +
                                                                                "\t\t    ht = bss[p+'ht'], vht = bss[p+'vht'],\n" +
                                                                                "\t\t\tmhz = bss[p+'mhz'], nss = bss[p+'nss'],\n" +
                                                                                "\t\t\tmcs = bss[p+'mcs'], sgi = bss[p+'short_gi'];\n" +
                                                                                "\n" +
                                                                                "\t\tif (ht || vht) {\n" +
                                                                                "\t\t\tif (vht) s += ', VHT-MCS %d'.format(mcs);\n" +
                                                                                "\t\t\tif (nss) s += ', VHT-NSS %d'.format(nss);\n" +
                                                                                "\t\t\tif (ht)  s += ', MCS %s'.format(mcs);\n" +
                                                                                "\t\t\tif (sgi) s += ', Short GI';\n" +
                                                                                "\t\t}\n" +
                                                                                "\n" +
                                                                                "\t\treturn s;\n" +
                                                                                "\t}\n" +
                                                                                "\n" +
                                                                                "\tfunction duid2mac(duid) {\n" +
                                                                                "\t\t// DUID-LLT / Ethernet\n" +
                                                                                "\t\tif (duid.length === 28 && duid.substr(0, 8) === '00010001')\n" +
                                                                                "\t\t\treturn duid.substr(16).replace(/(..)(?=..)/g, '$1:').toUpperCase();\n" +
                                                                                "\n" +
                                                                                "\t\t// DUID-LL / Ethernet\n" +
                                                                                "\t\tif (duid.length === 24 && duid.substr(0, 8) === '00030001')\n" +
                                                                                "\t\t\treturn duid.substr(8).replace(/(..)(?=..)/g, '$1:').toUpperCase();\n" +
                                                                                "\n" +
                                                                                "\t\treturn null;\n" +
                                                                                "\t}\n" +
                                                                                "\n" +
                                                                                "\tvar npoll = 1;\n" +
                                                                                "\tvar hosts = {\"C4:11:04:01:3C:75\":{\"ipv4\":\"192.168.1.241\"},\"18:31:BF:5D:DC:D8\":{\"ipv4\":\"192.168.1.1\"},\"44:8A:5B:F8:D7:56\":{\"ipv4\":\"192.168.1.240\"}};\n" +
                                                                                "\n" +
                                                                                "\tfunction updateHosts() {\n" +
                                                                                "\t\tXHR.get('/cgi-bin/luci/admin/status/overview', { hosts: 1 }, function(x, data) {\n" +
                                                                                "\t\t\thosts = data;\n" +
                                                                                "\t\t});\n" +
                                                                                "\t}\n" +
                                                                                "\n" +
                                                                                "\tXHR.poll(5, '/cgi-bin/luci/admin/status/overview', { status: 1 },\n" +
                                                                                "\t\tfunction(x, info)\n" +
                                                                                "\t\t{\n" +
                                                                                "\t\t\tif (!(npoll++ % 5))\n" +
                                                                                "\t\t\t\tupdateHosts();\n" +
                                                                                "\n" +
                                                                                "\t\t\tvar si = document.getElementById('wan4_i');\n" +
                                                                                "\t\t\tvar ss = document.getElementById('wan4_s');\n" +
                                                                                "\t\t\tvar ifc = info.wan;\n" +
                                                                                "\n" +
                                                                                "\t\t\tif (ifc && ifc.ifname && ifc.proto != 'none')\n" +
                                                                                "\t\t\t{\n" +
                                                                                "\t\t\t\tvar s = String.format(\n" +
                                                                                "\t\t\t\t\t'<strong>Type: </strong>%s<br />' +\n" +
                                                                                "\t\t\t\t\t'<strong>Address: </strong>%s<br />' +\n" +
                                                                                "\t\t\t\t\t'<strong>Netmask: </strong>%s<br />' +\n" +
                                                                                "\t\t\t\t\t'<strong>Gateway: </strong>%s<br />',\n" +
                                                                                "\t\t\t\t\t\tifc.proto,\n" +
                                                                                "\t\t\t\t\t\t(ifc.ipaddr) ? ifc.ipaddr : '0.0.0.0',\n" +
                                                                                "\t\t\t\t\t\t(ifc.netmask && ifc.netmask != ifc.ipaddr) ? ifc.netmask : '255.255.255.255',\n" +
                                                                                "\t\t\t\t\t\t(ifc.gwaddr) ? ifc.gwaddr : '0.0.0.0'\n" +
                                                                                "\t\t\t\t);\n" +
                                                                                "\n" +
                                                                                "\t\t\t\tfor (var i = 0; i < ifc.dns.length; i++)\n" +
                                                                                "\t\t\t\t{\n" +
                                                                                "\t\t\t\t\ts += String.format(\n" +
                                                                                "\t\t\t\t\t\t'<strong>DNS %d: </strong>%s<br />',\n" +
                                                                                "\t\t\t\t\t\ti + 1, ifc.dns[i]\n" +
                                                                                "\t\t\t\t\t);\n" +
                                                                                "\t\t\t\t}\n" +
                                                                                "\n" +
                                                                                "\t\t\t\tif (ifc.expires > -1)\n" +
                                                                                "\t\t\t\t{\n" +
                                                                                "\t\t\t\t\ts += String.format(\n" +
                                                                                "\t\t\t\t\t\t'<strong>Expires: </strong>%t<br />',\n" +
                                                                                "\t\t\t\t\t\tifc.expires\n" +
                                                                                "\t\t\t\t\t);\n" +
                                                                                "\t\t\t\t}\n" +
                                                                                "\n" +
                                                                                "\t\t\t\tif (ifc.uptime > 0)\n" +
                                                                                "\t\t\t\t{\n" +
                                                                                "\t\t\t\t\ts += String.format(\n" +
                                                                                "\t\t\t\t\t\t'<strong>Connected: </strong>%t<br />',\n" +
                                                                                "\t\t\t\t\t\tifc.uptime\n" +
                                                                                "\t\t\t\t\t);\n" +
                                                                                "\t\t\t\t}\n" +
                                                                                "\n" +
                                                                                "\t\t\t\tss.innerHTML = String.format('<small>%s</small>', s);\n" +
                                                                                "\t\t\t\tsi.innerHTML = String.format(\n" +
                                                                                "\t\t\t\t\t'<img src=\"/luci-static/resources/icons/ethernet.png\" />' +\n" +
                                                                                "\t\t\t\t\t'<br /><small><a href=\"%s\">%s</a></small>',\n" +
                                                                                "\t\t\t\t\t\tifc.link, ifc.ifname\n" +
                                                                                "\t\t\t\t);\n" +
                                                                                "\t\t\t}\n" +
                                                                                "\t\t\telse\n" +
                                                                                "\t\t\t{\n" +
                                                                                "\t\t\t\tsi.innerHTML = '<img src=\"/luci-static/resources/icons/ethernet_disabled.png\" /><br /><small>?</small>';\n" +
                                                                                "\t\t\t\tss.innerHTML = '<em>Not connected</em>';\n" +
                                                                                "\t\t\t}\n" +
                                                                                "\n" +
                                                                                "\t\t\t\n" +
                                                                                "\n" +
                                                                                "\t\t\t\n" +
                                                                                "\n" +
                                                                                "\t\t\t\n" +
                                                                                "\n" +
                                                                                "\t\t\t\n" +
                                                                                "\n" +
                                                                                "\t\t\tvar e;\n" +
                                                                                "\n" +
                                                                                "\t\t\tif (e = document.getElementById('localtime'))\n" +
                                                                                "\t\t\t\te.innerHTML = info.localtime;\n" +
                                                                                "\n" +
                                                                                "\t\t\tif (e = document.getElementById('uptime'))\n" +
                                                                                "\t\t\t\te.innerHTML = String.format('%t', info.uptime);\n" +
                                                                                "\n" +
                                                                                "\t\t\tif (e = document.getElementById('loadavg'))\n" +
                                                                                "\t\t\t\te.innerHTML = String.format(\n" +
                                                                                "\t\t\t\t\t'%.02f, %.02f, %.02f',\n" +
                                                                                "\t\t\t\t\tinfo.loadavg[0] / 65535.0,\n" +
                                                                                "\t\t\t\t\tinfo.loadavg[1] / 65535.0,\n" +
                                                                                "\t\t\t\t\tinfo.loadavg[2] / 65535.0\n" +
                                                                                "\t\t\t\t);\n" +
                                                                                "\n" +
                                                                                "\t\t\tif (e = document.getElementById('memtotal'))\n" +
                                                                                "\t\t\t\te.innerHTML = progressbar(\n" +
                                                                                "\t\t\t\t\t((info.memory.free + info.memory.buffered) / 1024) + \" kB\",\n" +
                                                                                "\t\t\t\t\t(info.memory.total / 1024) + \" kB\"\n" +
                                                                                "\t\t\t\t);\n" +
                                                                                "\n" +
                                                                                "\t\t\tif (e = document.getElementById('memfree'))\n" +
                                                                                "\t\t\t\te.innerHTML = progressbar(\n" +
                                                                                "\t\t\t\t\t(info.memory.free / 1024) + \" kB\",\n" +
                                                                                "\t\t\t\t\t(info.memory.total / 1024) + \" kB\"\n" +
                                                                                "\t\t\t\t);\n" +
                                                                                "\n" +
                                                                                "\t\t\tif (e = document.getElementById('membuff'))\n" +
                                                                                "\t\t\t\te.innerHTML = progressbar(\n" +
                                                                                "\t\t\t\t\t(info.memory.buffered / 1024) + \" kB\",\n" +
                                                                                "\t\t\t\t\t(info.memory.total / 1024) + \" kB\"\n" +
                                                                                "\t\t\t\t);\n" +
                                                                                "\n" +
                                                                                "\t\t\tif (e = document.getElementById('swaptotal'))\n" +
                                                                                "\t\t\t\te.innerHTML = progressbar(\n" +
                                                                                "\t\t\t\t\t(info.swap.free / 1024) + \" kB\",\n" +
                                                                                "\t\t\t\t\t(info.swap.total / 1024) + \" kB\"\n" +
                                                                                "\t\t\t\t);\n" +
                                                                                "\n" +
                                                                                "\t\t\tif (e = document.getElementById('swapfree'))\n" +
                                                                                "\t\t\t\te.innerHTML = progressbar(\n" +
                                                                                "\t\t\t\t\t(info.swap.free / 1024) + \" kB\",\n" +
                                                                                "\t\t\t\t\t(info.swap.total / 1024) + \" kB\"\n" +
                                                                                "\t\t\t\t);\n" +
                                                                                "\n" +
                                                                                "\t\t\tif (e = document.getElementById('conns'))\n" +
                                                                                "\t\t\t\te.innerHTML = progressbar(info.conncount, info.connmax);\n" +
                                                                                "\n" +
                                                                                "\t\t}\n" +
                                                                                "\t);\n" +
                                                                                "//]]></script>\n" +
                                                                                "\n" +
                                                                                "<h2 name=\"content\">Status</h2>\n" +
                                                                                "\n" +
                                                                                "<fieldset class=\"cbi-section\">\n" +
                                                                                "\t<legend>System</legend>\n" +
                                                                                "\n" +
                                                                                "\t<table width=\"100%\" cellspacing=\"10\">\n" +
                                                                                "\t\t<tr><td width=\"33%\">Model</td><td>WhatsMiner M20S</td></tr>\n" +
                                                                                "        <tr><td width=\"33%\">Hostname</td><td>M20S.HB20.H3-V10.P21-H3M14S6F200114K10102-V04-192104B</td></tr>\n" +
                                                                                "\t\t<tr><td width=\"33%\">Firmware Version</td><td>20200917.22.REL</td></tr>\n" +
                                                                                "\t\t<tr><td width=\"33%\">Kernel Version</td><td>#401 SMP PREEMPT Thu Sep 17 22:22:57 CST 2020</td></tr>\n" +
                                                                                "\t\t<tr><td width=\"33%\">CGMiner Version</td><td>4.9.2-git-e94d7c9</td></tr>\n" +
                                                                                "\t\t<tr><td width=\"33%\">Local Time</td><td id=\"localtime\">-</td></tr>\n" +
                                                                                "\t\t<tr><td width=\"33%\">Uptime</td><td id=\"uptime\">-</td></tr>\n" +
                                                                                "\t\t<tr><td width=\"33%\">Load Average</td><td id=\"loadavg\">-</td></tr>\n" +
                                                                                "\t</table>\n" +
                                                                                "</fieldset>\n" +
                                                                                "\n" +
                                                                                "<fieldset class=\"cbi-section\">\n" +
                                                                                "\t<legend>Memory</legend>\n" +
                                                                                "\n" +
                                                                                "\t<table width=\"100%\" cellspacing=\"10\">\n" +
                                                                                "\t\t<tr><td width=\"33%\">Total Available</td><td id=\"memtotal\">-</td></tr>\n" +
                                                                                "\t\t<tr><td width=\"33%\">Free</td><td id=\"memfree\">-</td></tr>\n" +
                                                                                "\t\t<tr><td width=\"33%\">Buffered</td><td id=\"membuff\">-</td></tr>\n" +
                                                                                "\t</table>\n" +
                                                                                "</fieldset>\n" +
                                                                                "\n" +
                                                                                "\n" +
                                                                                "\n" +
                                                                                "<fieldset class=\"cbi-section\">\n" +
                                                                                "\t<legend>Network</legend>\n" +
                                                                                "\n" +
                                                                                "\t<table width=\"100%\" cellspacing=\"10\">\n" +
                                                                                "\t\t<tr><td width=\"33%\" style=\"vertical-align:top\">IPv4 WAN Status</td><td>\n" +
                                                                                "\t\t\t<table><tr>\n" +
                                                                                "\t\t\t\t<td id=\"wan4_i\" style=\"width:16px; text-align:center; padding:3px\"><img src=\"/luci-static/resources/icons/ethernet_disabled.png\" /><br /><small>?</small></td>\n" +
                                                                                "\t\t\t\t<td id=\"wan4_s\" style=\"vertical-align:middle; padding: 3px\"><em>Collecting data...</em></td>\n" +
                                                                                "\t\t\t</tr></table>\n" +
                                                                                "\t\t</td></tr>\n" +
                                                                                "\t\t\n" +
                                                                                "\t\t<tr><td width=\"33%\">Active Connections</td><td id=\"conns\">-</td></tr>\n" +
                                                                                "\t</table>\n" +
                                                                                "</fieldset>\n" +
                                                                                "\n" +
                                                                                "\n" +
                                                                                "\n" +
                                                                                "\n" +
                                                                                "\n" +
                                                                                "\n" +
                                                                                "   <footer>\n" +
                                                                                "    <a href=\"https://github.com/openwrt/luci\">Powered by LuCI Master (git-16.336.70424-1fd43b4)</a> / OpenWrt Designated Driver 50045\n" +
                                                                                "    \n" +
                                                                                "   </footer>\n" +
                                                                                "   </div>\n" +
                                                                                "  </div>\n" +
                                                                                " </body>\n" +
                                                                                "</html>",
                                                                        Collections.emptyMap())))),
                                WhatsminerType.WHATSMINER_M20S
                        }
                });
    }

    /**
     * Creates test arguments.
     *
     * @return The arguments.
     */
    private static Map<String, Object> toArgs() {
        return ImmutableMap.of(
                "username",
                "username",
                "password",
                "password",
                "webPort",
                "8080");
    }
}

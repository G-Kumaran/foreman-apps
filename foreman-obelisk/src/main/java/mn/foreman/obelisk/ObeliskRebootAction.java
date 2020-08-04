package mn.foreman.obelisk;

import mn.foreman.model.AsicAction;
import mn.foreman.model.error.MinerException;
import mn.foreman.obelisk.json.Dashboard;

import java.util.Map;

/** Reboots an obelisk. */
public class ObeliskRebootAction
        implements AsicAction.CompletableAction {

    @Override
    public boolean run(
            final String ip,
            final int port,
            final Map<String, Object> parameters)
            throws MinerException {
        try {
            final String username =
                    (String) parameters.getOrDefault("username", "");
            final String password =
                    (String) parameters.getOrDefault("password", "");
            ObeliskQuery.runSessionQuery(
                    ObeliskQuery.Context
                            .<Dashboard>builder()
                            .apiIp(ip)
                            .apiPort(port)
                            .uri("/api/action/reboot")
                            .method("POST")
                            .username(username)
                            .password(password)
                            .isReboot(true)
                            .build());
        } catch (final Exception e) {
            throw new MinerException(e);
        }
        return true;
    }
}
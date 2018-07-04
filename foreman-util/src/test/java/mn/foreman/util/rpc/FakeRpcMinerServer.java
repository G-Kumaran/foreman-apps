package mn.foreman.util.rpc;

import mn.foreman.util.AbstractFakeMinerServer;
import mn.foreman.util.http.FakeHttpMinerServer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * A {@link FakeHttpMinerServer} provides a fake RPC-based miner API that can be
 * leveraged for integration testing.
 */
public class FakeRpcMinerServer
        extends AbstractFakeMinerServer<RpcHandler> {

    /** The logger for this class. */
    private static final Logger LOG =
            LoggerFactory.getLogger(FakeRpcMinerServer.class);

    /** A reusable thread pool. */
    private static final Executor THREAD_POOL =
            Executors.newSingleThreadExecutor();

    /** The server. */
    private ServerSocket serverSocket;

    /**
     * Constructor.
     *
     * @param port     The port.
     * @param handlers The handlers.
     */
    public FakeRpcMinerServer(
            final int port,
            final Map<String, RpcHandler> handlers) {
        super(port, handlers);
    }

    @Override
    public void close() throws Exception {
        this.serverSocket.close();
    }

    @Override
    public void start() {
        try {
            this.serverSocket = new ServerSocket(this.port);

            THREAD_POOL.execute(() -> {
                while (true) {
                    try (final Socket socket = this.serverSocket.accept()) {
                        String request = "";
                        final InputStream inputStream =
                                socket.getInputStream();
                        while (true) {
                            request += (char) inputStream.read();
                            if (this.handlers.containsKey(request)) {
                                this.handlers.get(request).process(socket);
                                break;
                            }
                        }
                        socket.close();
                    } catch (final IOException ioe) {
                        LOG.warn("Exception occurred", ioe);
                        break;
                    }
                }
            });
        } catch (final IOException ioe) {
            LOG.warn("Exception occurred", ioe);
        }
    }
}
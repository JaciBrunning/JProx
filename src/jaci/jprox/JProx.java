package jaci.jprox;

import jaci.jprox.interfaces.ConnectionListener;
import jaci.jprox.interfaces.RewriteHandler;
import jaci.jprox.proxy.ThreadProxy;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class JProx implements Runnable {

    int port;
    ServerSocket socket;
    Vector<ConnectionListener> connectionListeners = new Vector<ConnectionListener>();
    Vector<RewriteHandler> rewriteHandlers = new Vector<RewriteHandler>();

    public JProx(int port) {
        this.port = port;
    }

    public void startProxyServer() throws IOException {
        socket = new ServerSocket(port);
        Thread thread = new Thread(this);
        thread.setName("JProx Proxy Dispatcher");
        thread.start();
    }

    public void registerConnectionListener(ConnectionListener listener) {
        connectionListeners.add(listener);
    }

    public void registerRewriteHandler(RewriteHandler handler) {
        rewriteHandlers.add(handler);
    }

    public Vector<RewriteHandler> getRewriteHandlers() {
        return rewriteHandlers;
    }
    @Override
    public void run() {
        loop:
        while (!socket.isClosed()) {
            try {
                Socket client = socket.accept();
                for (ConnectionListener listener : connectionListeners)
                    if (listener.disallowConnection(client)) {
                        client.close();
                        continue loop;
                    }
                ThreadProxy proxyThread = new ThreadProxy(client, this);
                proxyThread.start();
            } catch (Exception e) {}
        }
    }
}

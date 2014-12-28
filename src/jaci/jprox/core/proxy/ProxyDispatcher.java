package jaci.jprox.core.proxy;

import jaci.jprox.lib.Preferences;
import jaci.jprox.lib.event.EventClientConnected;
import jaci.jprox.lib.event.JProxEventBus;
import jaci.jprox.lib.log.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ProxyDispatcher {

    public static void start() {
        Thread threadSearch = new Thread() {
            public void run() {
                try {
                    int port = (Integer) Preferences.blackboard.get("config:port");
                    ServerSocket socket = new ServerSocket(port);
                    Preferences.blackboard.put("proxy:socket", socket);
                    Preferences.blackboard.put("proxy:alive", true);
                    Logger.info("Started Server Proxy Socket on Port: " + port, true);


                    while (true == Preferences.blackboard.get("proxy:alive")) {
                        Socket clientSocket = socket.accept();

                        String IP = clientSocket.getInetAddress().toString();

                        EventClientConnected event = new EventClientConnected(clientSocket, IP);
                        JProxEventBus.instance().raiseEvent(event);

                        if (event.isCancelled()) {
                            clientSocket.close();
                            Logger.info("Disconnected Client: " + IP + " (event cancelled)", false);
                        } else {
                            Logger.info("Client Connected: " + IP, false);
                            new ThreadProxy(clientSocket, IP);
                        }
                    }
                } catch (Exception e) { }
                Logger.info("Shutting down Proxy on port...",true);
            }
        };
        threadSearch.start();
    }


}

package jaci.jprox.interfaces;

import java.net.Socket;

public interface ConnectionListener {

    /**
     * Return true if you want to prevent the socket from connecting to the server
     */
    public boolean disallowConnection(Socket socket);

}

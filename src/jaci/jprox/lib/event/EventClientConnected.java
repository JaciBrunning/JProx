package jaci.jprox.lib.event;

import sourcecoded.events.AbstractEvent;
import sourcecoded.events.annotation.Cancelable;

import java.net.Socket;

@Cancelable
public class EventClientConnected extends AbstractEvent {

    Socket clientSocket;
    String IP;

    /**
     * Called when a client tries to connect, can be
     * cancelled if you do not want to accept a connection
     * from this client.
     *
     * This is called whenever a client makes a request
     *
     * IP is represented as *.*.*.* as opposed to a hostname
     * in order to reduce DNS-Lookup time.
     */
    public EventClientConnected(Socket client, String IP) {
        this.clientSocket = client;
        this.IP = IP;
    }

}

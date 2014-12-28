package jaci.jprox.lib.event;

import sourcecoded.events.AbstractEvent;
import sourcecoded.events.annotation.Cancelable;

import java.net.Socket;
import java.util.HashMap;

@Cancelable
public class EventHTTPRequest extends AbstractEvent {

    public Socket clientSocket;
    public HashMap<String, Object> requestInfo;
    public HashMap<String, String> headers;

    /**
     * Called when a HTTP request has been parsed
     * RequestInfo contains the URL, Method and Protocol
     *
     * Cancel this event to deny access to the resource
     */
    public EventHTTPRequest(Socket client, HashMap<String, Object> requestInfo, HashMap<String, String> headers) {
        this.clientSocket = client;
        this.requestInfo = requestInfo;
        this.headers = headers;
    }

}

package jaci.jprox.interfaces;

import jaci.jprox.proxy.ThreadProxy;

import java.net.Socket;
import java.util.HashMap;

public interface RewriteHandler {

    public String rewriteURL(String url, HashMap<String, String> headers, HashMap<String, Object> requestInfo, Socket socket, ThreadProxy proxy);

}

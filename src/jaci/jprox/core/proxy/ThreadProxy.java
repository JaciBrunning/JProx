package jaci.jprox.core.proxy;

import jaci.jprox.lib.event.EventHTTPRequest;
import jaci.jprox.lib.event.JProxEventBus;
import jaci.jprox.lib.log.Logger;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ThreadProxy extends Thread {

    Socket socket;
    private static final int BUFFER_SIZE = 32768;

    public ThreadProxy(Socket clientSocket, String IP) {
        this.socket = clientSocket;
        this.setName("Proxy Thread: " + IP);
        this.start();
    }

    @Override
    public void run() {
        try {
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            BufferedReader /*you should want a bad bitch like*/ dis = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            HashMap<String, String> headers = new HashMap<String, String>();
            HashMap<String, Object> requestInfo = new HashMap<String, Object>();

            String in;
            int count = 0;
            while ((in = dis.readLine()) != null && !in.equals("")) {
                Logger.info("Request for: " + in, false);
                parseRequests(headers, requestInfo, in, count);
                count++;
            }

            Logger.info("Request parsing complete", false);
            EventHTTPRequest event = new EventHTTPRequest(socket, requestInfo, headers);
            JProxEventBus.instance().raiseEvent(event);
            if (event.isCancelled()) {
                Logger.warn("Access to resource denied: " + requestInfo.get("URL") + " (event cancelled)");
                return;
            }

            URL url = new URL((String) requestInfo.get("URL"));
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod((String) requestInfo.get("Method"));

            for (Map.Entry<String, String> entry : headers.entrySet())
                conn.setRequestProperty(entry.getKey(), entry.getValue());

            int responseCode = conn.getResponseCode();
            Logger.info("Server replied with response: " + responseCode + " (" + conn.getResponseMessage() +")", false);

            InputStream is = conn.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));

            byte by[] = new byte[BUFFER_SIZE];
            int index = is.read(by, 0, BUFFER_SIZE);
            while (index != -1) {
                dos.write(by, 0, index);
                index = is.read(by, 0, BUFFER_SIZE);
            }
            dos.flush();

            rd.close();
            dos.close();
            dis.close();

            if (socket != null) {
                socket.close();
            }

        } catch (Exception e) {
            Logger.error("Could not proxy for client: " + e);
            e.printStackTrace();
        }
    }

    public static void parseRequests(HashMap<String, String> headers, HashMap<String, Object> requestInfo, String request, int count) {
        if (request == null)
            return;

        String[] parsed = request.split(" ");
        if (parsed.length == 0) return;

        if (parsed[0].startsWith("Accept-Encoding")) return;

        if (count == 0) {
            requestInfo.put("Method", parsed[0]);
            requestInfo.put("URL", parsed[1]);
            requestInfo.put("Protocol", parsed[2]);
        } else {
            try {
                String req = request.replace(parsed[0], "");
                if (req.startsWith(" "))
                    req = req.substring(1);

                headers.put(parsed[0].substring(0, parsed[0].lastIndexOf(':')), req);
            } catch (Exception e) {
            }
        }

    }

}

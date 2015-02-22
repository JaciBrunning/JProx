package jaci.jprox.proxy;

import jaci.jprox.JProx;
import jaci.jprox.Logger;
import jaci.jprox.interfaces.RewriteHandler;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ThreadProxy extends Thread {

    Socket client;
    JProx parent;

    static int BUFFER_SIZE = 32768;

    public ThreadProxy(Socket socket, JProx parent) {
        this.client = socket;
        this.parent = parent;
        this.setName("JProxy Client");
    }

    @Override
    public void run() {
        try {
            DataOutputStream dos = new DataOutputStream(client.getOutputStream());
            BufferedReader dis = new BufferedReader(new InputStreamReader(client.getInputStream()));

            HashMap<String, String> headers = new HashMap<String, String>();
            HashMap<String, Object> requestInfo = new HashMap<String, Object>();

            String in;
            int count = 0;
            while ((in = dis.readLine()) != null && !in.equals("")) {
                parseRequests(headers, requestInfo, in, count);
                count++;
            }

            boolean ssl = false;
            if (requestInfo.get("Method").equals("CONNECT")) {
                ssl = true;
            }

            String url = (String) requestInfo.get("URL");
            for (RewriteHandler rw : parent.getRewriteHandlers())
                url = rw.rewriteURL(url, headers, requestInfo, client, this);

            Pattern pattern = Pattern.compile("(https?://)?([^:^/]*)(:\\d*)?(.*)?");
            Matcher matcher = pattern.matcher(url);

            matcher.find();

            String protocol = matcher.group(1);
            String domain = matcher.group(2);
            String port = matcher.group(3);
            String uri = matcher.group(4);

            if (ssl || (protocol != null && protocol.equals("https://")) || (port != null && port.equals(":443"))) {
                Socket socket = new Socket(domain, port == null ? 443 : Integer.parseInt(port.substring(1)));
                InputStream stream = socket.getInputStream();
                final DataOutputStream outStream = new DataOutputStream(socket.getOutputStream());

                String resp = "HTTP/1.0 200 Connection established\n" +
                        "Proxy-agent: JProx/1.0\n\r\n";
                dos.writeBytes(resp);

                final InputStream cIn = client.getInputStream();
                new Thread() {
                    public void run() {
                        try {
                            tunnelStream(cIn, outStream);
                        } catch (IOException e) { }
                    }
                }.start();

                try {
                    tunnelStream(stream, dos);
                } catch(Exception e) {}

                stream.close();
                socket.close();
            } else {
                URL urlI = new URL((protocol == null ? "http://" : protocol) + domain + (port == null ? "" : port) + uri);
                HttpURLConnection conn = (HttpURLConnection) urlI.openConnection();
                conn.setRequestMethod((String) requestInfo.get("Method"));

                for (Map.Entry<String, String> entry : headers.entrySet())
                    conn.setRequestProperty(entry.getKey(), entry.getValue());

                InputStream is = conn.getInputStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(is));

                byte by[] = new byte[BUFFER_SIZE];
                int index = is.read(by, 0, BUFFER_SIZE);
                while (index != -1) {
                    dos.write(by, 0, index);
                    index = is.read(by, 0, BUFFER_SIZE);
                }
                rd.close();
                conn.disconnect();
            }

            dos.close();
            dis.close();

            if (client != null) {
                client.close();
            }

        } catch (Exception e) {
            Logger.warn("Could Not Proxy for Client: " + client);
            e.printStackTrace();
        }
    }

    public static void tunnelStream(InputStream input, OutputStream output) throws IOException {
        byte[] buffer = new byte[BUFFER_SIZE];
        int bytesRead;
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
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

package jaci.jprox;

import java.io.IOException;

public class JProxTest {

    public static void main(String[] args) {
        JProx prox = new JProx(8888);
        try {
            prox.startProxyServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

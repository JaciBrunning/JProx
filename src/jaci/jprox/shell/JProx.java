package jaci.jprox.shell;

import jaci.jprox.core.config.ConfigHandler;
import jaci.jprox.core.proxy.ProxyDispatcher;
import jaci.jprox.lib.Preferences;
import jaci.jprox.lib.event.JProxEventBus;
import jaci.jprox.lib.log.Logger;

import java.io.IOException;

public class JProx {

    public static void main(String[] args) throws IOException {
        Logger.info("JProx Starting...", true);

        Logger.info("Starting Preferences...", true);
        Preferences.init();

        Logger.info("Loading Configuration File...", true);
        ConfigHandler.load();

        ProxyDispatcher.start();
    }
}

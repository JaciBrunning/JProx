package jaci.jprox.lib.event;

import sourcecoded.events.EventBus;

public class JProxEventBus extends EventBus {

    private static JProxEventBus instance;
    public static JProxEventBus instance() {
        if (instance == null)
            instance = new JProxEventBus();
        return instance;
    }

}

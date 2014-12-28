package jaci.jprox.lib;

import java.io.File;
import java.util.HashMap;

public class Preferences {

    public static HashMap<String, Object> blackboard = new HashMap<String, Object>();

    public static void init() {
        blackboard.put("launch:configFile", new File("jprox.cfg"));
    }

}

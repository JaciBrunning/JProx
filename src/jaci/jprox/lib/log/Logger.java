package jaci.jprox.lib.log;

import jaci.jprox.lib.Preferences;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Logger {

    public static void info(String m, boolean bypassVerbose) {
        if (bypassVerbose || (Boolean)Preferences.blackboard.get("config:verbose"))
            System.out.println(String.format("[%s] [JProx] [INFO] [%s] %s", getTime(), Thread.currentThread().getName(), m));
    }

    public static void warn(String m) {
        System.out.println(String.format("[%s] [JProx] [WARN] [%s] %s", getTime(), Thread.currentThread().getName(), m));
    }

    public static void error(String m) {
        System.err.println(String.format("[%s] [JProx] [ERROR] [%s] %s", getTime(), Thread.currentThread().getName(), m));
    }

    static String getTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        return sdf.format(new Date());
    }

}

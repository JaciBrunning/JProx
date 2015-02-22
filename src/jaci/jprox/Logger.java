package jaci.jprox;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.prefs.Preferences;

public class Logger {

    public static void info(String m) {
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
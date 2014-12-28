package jaci.jprox.core.config;

import com.google.gson.*;
import com.google.gson.stream.JsonWriter;
import jaci.jprox.lib.Preferences;
import jaci.jprox.lib.event.EventConfigLoad;
import jaci.jprox.lib.event.JProxEventBus;
import jaci.jprox.lib.log.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ConfigHandler {

    private static boolean requiresReload = false;

    public static void load() throws IOException {
        File config = (File) Preferences.blackboard.get("launch:configFile");

        JsonObject object = null;
        if (config.exists())
            object = new JsonParser().parse(new FileReader(config)).getAsJsonObject();
        if (object == null)
            object = new JsonObject();

        loadProperties(object);

        if (requiresReload) {
            Logger.info("Reloading Configuration File...", true);
            String s = new Gson().toJson(object);
            FileWriter writer = new FileWriter(config);
            writer.write(s);
            writer.close();
        }
    }

    public static void loadProperties(JsonObject parent) {
        property(parent, "port", Type.INTEGER, 8888);
        property(parent, "verbose", Type.BOOLEAN, true);

        JProxEventBus.instance().raiseEvent(new EventConfigLoad(parent));
    }

    public static Object property(JsonObject object, String name, Type type, Object defaultVal) {
        if (object.has(name)) {
            Preferences.blackboard.put("config:" + name, parseType(object.get(name), type));
            return Preferences.blackboard.get("config:" + name);
        } else {
            createType(object, name, type, defaultVal);
            return property(object, name, type, defaultVal);
        }
    }

    public static Object parseType(JsonElement element, Type type) {
        switch (type) {
            case INTEGER:
                return element.getAsInt();
            case DOUBLE:
                return element.getAsDouble();
            case STRING:
                return element.getAsString();
            case FLOAT:
                return element.getAsFloat();
            case LONG:
                return element.getAsLong();
            case BYTE:
                return element.getAsByte();
            case BOOLEAN:
                return element.getAsBoolean();

            default:
                return element;
        }
    }

    public static void createType(JsonObject object, String name, Type type, Object defaultValue) {
        switch (type) {
            case INTEGER:
                object.add(name, new JsonPrimitive((Integer)defaultValue));
                break;
            case DOUBLE:
                object.add(name, new JsonPrimitive((Double)defaultValue));
                break;
            case STRING:
                object.add(name, new JsonPrimitive((String)defaultValue));
                break;
            case FLOAT:
                object.add(name, new JsonPrimitive((Float)defaultValue));
                break;
            case LONG:
                object.add(name, new JsonPrimitive((Long)defaultValue));
                break;
            case BYTE:
                object.add(name, new JsonPrimitive((Byte)defaultValue));
                break;
            case BOOLEAN:
                object.add(name, new JsonPrimitive((Boolean)defaultValue));
                break;
        }
        requiresReload = true;
    }

    public static enum Type {
        INTEGER,
        DOUBLE,
        STRING,
        FLOAT,
        LONG,
        BYTE,
        BOOLEAN;
    }

}

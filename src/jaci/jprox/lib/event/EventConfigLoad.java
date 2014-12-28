package jaci.jprox.lib.event;

import com.google.gson.JsonObject;
import sourcecoded.events.AbstractEvent;

public class EventConfigLoad extends AbstractEvent {

    public JsonObject parentObject;
    public EventConfigLoad(JsonObject parentObject) {
        this.parentObject = parentObject;
    }

}

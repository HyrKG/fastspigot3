package cn.hyrkg.fastspigot3.plugin.lifecycle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LifeRecorder {

    private static final List<String> EVENTS = new ArrayList<>();

    public static void record(String msg) {
        EVENTS.add(msg);
    }

    public static List<String> events() {
        return Collections.unmodifiableList(EVENTS);
    }

    public static void reset() {
        EVENTS.clear();
    }
}



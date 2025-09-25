package cn.hyrkg.fastspigot3.test.lifecycle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LifeRecorder {

    private static final List<String> EVENTS = new ArrayList<>();

    public static void record(String msg) {
        System.out.println(msg);
    }

    public static List<String> events() {
        return Collections.unmodifiableList(EVENTS);
    }

    public static void reset() {
        EVENTS.clear();
    }
}



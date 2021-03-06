package com.hyd.dao.mate.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Listeners {

    private static final Map<Events, List<Runnable>> listeners = new HashMap<>();

    public static void addListener(Events event, Runnable listener) {
        listeners
            .computeIfAbsent(event, _e -> new ArrayList<>())
            .add(listener);
    }

    public static void publish(Events event) {
        listeners
            .getOrDefault(event, Collections.emptyList())
            .forEach(Runnable::run);
    }
}

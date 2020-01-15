package com.hyd.dao.mate.util;

import java.util.*;

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

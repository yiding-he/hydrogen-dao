package com.hyd.dao.mate.util;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public class MapCacheUtils {

    public MapCacheUtils() {
    }

    @SuppressWarnings("unchecked")
    public static <K, V> Map<K, V> newLRUCache(final int size, boolean threadSafe) {
        LinkedHashMap cache = new LinkedHashMap(size + 1, 0.75F, true) {

            public boolean removeEldestEntry(Entry eldest) {
                return this.size() > size;
            }
        };
        return threadSafe ? Collections.synchronizedMap(cache) : cache;
    }
}

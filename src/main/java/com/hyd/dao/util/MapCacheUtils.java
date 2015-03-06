package com.hyd.dao.util;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class MapCacheUtils {

    public MapCacheUtils() {
    }

    public static Map newLRUCache(final int size, boolean threadSafe) {
        LinkedHashMap cache = new LinkedHashMap(size + 1, 0.75F, true) {

            public boolean removeEldestEntry(Map.Entry eldest) {
                return this.size() > size;
            }
        };
        return threadSafe ? Collections.synchronizedMap(cache) : cache;
    }
}

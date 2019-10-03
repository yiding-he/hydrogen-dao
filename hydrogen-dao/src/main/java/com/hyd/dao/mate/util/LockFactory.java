package com.hyd.dao.mate.util;

import java.util.Map;

@SuppressWarnings("unchecked")
public class LockFactory {

    public static final int CACHE_SIZE = 10000;

    private static final Map<Integer, Object> CACHE = MapCacheUtils.newLRUCache(CACHE_SIZE, false);

    public LockFactory() {
    }

    public static synchronized Object getLock(String cacheKey) {
        int hash = cacheKey.hashCode();
        Object lock;

        if (!CACHE.containsKey(hash)) {
            lock = new Object();
            CACHE.put(hash, lock);
        } else {
            lock = CACHE.get(hash);
        }

        return lock;
    }
}

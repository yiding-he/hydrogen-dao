package com.hyd.dao.util;

import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

/**
 * Lock and synchronization by string key.
 *
 * @author yidin
 */
public class Locker {

    public static final int CACHE_SIZE = 10000;

    private static final Map<String, Lock> CACHE = MapCacheUtils.newLRUCache(CACHE_SIZE, false);

    private static final Lock CACHE_LOCK = new ReentrantLock();

    public static void lockAndRun(String key, Runnable runnable) {

        if (!CACHE.containsKey(key)) {
            lockAndRun(CACHE_LOCK, () -> {
                if (!CACHE.containsKey(key)) {
                    CACHE.put(key, new ReentrantLock());
                }
            });
        }

        lockAndRun(CACHE.get(key), runnable);
    }

    public static <T> T lockAndRun(String key, Supplier<T> supplier) {

        if (!CACHE.containsKey(key)) {
            lockAndRun(CACHE_LOCK, () -> {
                if (!CACHE.containsKey(key)) {
                    CACHE.put(key, new ReentrantLock());
                }
            });
        }

        return lockAndRun(CACHE.get(key), supplier);
    }

    private static void lockAndRun(Lock lock, Runnable runnable) {
        try {
            lock.lock();
            runnable.run();
        } finally {
            lock.unlock();
        }
    }

    private static <T> T lockAndRun(Lock lock, Supplier<T> supplier) {
        try {
            lock.lock();
            return supplier.get();
        } finally {
            lock.unlock();
        }
    }
}

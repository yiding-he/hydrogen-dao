package com.hyd.dao.mate.util;

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
        lockAndRun(getCacheLock(key), runnable);
    }

    public static <T> T lockAndRun(String key, Supplier<T> supplier) {
        return lockAndRun(getCacheLock(key), supplier);
    }

    private static Lock getCacheLock(String key) {
        Lock[] lock = new Lock[]{CACHE.get(key)};
        if (lock[0] == null) {
            lockAndRun(CACHE_LOCK, () -> {
                lock[0] = CACHE.get(key);
                if (lock[0] == null) {
                    lock[0] = new ReentrantLock();
                    CACHE.put(key, lock[0]);
                }
            });
        }
        return lock[0];
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

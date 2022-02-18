package com.hyd.dao.time;

import java.time.format.DateTimeFormatter;
import java.util.Map;

import static com.hyd.dao.mate.util.MapCacheUtils.newLRUCache;

/**
 * DateTimeFormatter 的一个缓存池
 */
public class TimeFormatters {

    /**
     * 最多缓存多少个不同格式的 DateTimeFormatter 实例。超过的话按 LRU 策略删除
     */
    public static final int DEFAULT_POOL_SIZE = 100;

    private static Map<String, DateTimeFormatter> cache = newLRUCache(DEFAULT_POOL_SIZE, true);

    /**
     * 万一默认缓存池大小不够，可以在应用启动时通过本方法修改缓存大小
     *
     * @param newPoolSize 新的缓存大小
     */
    public static void adjustPoolSize(int newPoolSize) {
        cache = newLRUCache(newPoolSize, true);
    }

    public static DateTimeFormatter ofPattern(String pattern) {
        return cache.computeIfAbsent(pattern, DateTimeFormatter::ofPattern);
    }
}

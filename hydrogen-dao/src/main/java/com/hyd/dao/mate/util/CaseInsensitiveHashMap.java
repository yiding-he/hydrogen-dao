package com.hyd.dao.mate.util;

import java.io.Serial;
import java.util.HashMap;
import java.util.Map;

/**
 * 对 key 忽略大小写的 HashMap
 */
public class CaseInsensitiveHashMap<V> extends HashMap<String, V> {

    @Serial
    private static final long serialVersionUID = 1L;

    // lowercase -> original
    private final Map<String, String> originalKeys = new HashMap<>();

    /**
     * 根据 key 获取值
     *
     * @param key 键
     *
     * @return 相对应的值
     *
     * @throws IllegalArgumentException 如果 key 不是一个字符串
     */
    public V get(String key) {
        var originalKey = originalKeys.get(key.toLowerCase());
        if (originalKey == null) {
            return null;
        } else {
            return super.get(originalKey);
        }
    }

    /**
     * 设置值
     *
     * @param key   键
     * @param value 值
     *
     * @throws IllegalArgumentException 如果 key 不是一个字符串
     */
    public V put(String key, V value) {
        var lcKey = key.toLowerCase();
        var originalKey = originalKeys.get(lcKey);
        if (originalKey != null) {
            super.remove(originalKey);
        }

        originalKeys.put(lcKey, key);
        return super.put(key, value);
    }

    /**
     * 检查 key 是否存在
     *
     * @param key 要检查的 key
     *
     * @return 如果存在则返回 true
     */
    @Override
    public boolean containsKey(Object key) {
        if (!(key instanceof String)) {
            throw new IllegalArgumentException("Key must be a string.");
        }

        var lcKey = ((String) key).toLowerCase();
        return originalKeys.containsKey(lcKey);
    }

    @Override
    public V remove(Object key) {
        if (!(key instanceof String)) {
            throw new IllegalArgumentException("Key must be a string.");
        }

        var lcKey = ((String) key).toLowerCase();
        var originalKey = originalKeys.get(lcKey);
        originalKeys.remove(lcKey);
        return super.remove(originalKey);
    }

    @Override
    public boolean remove(Object key, Object value) {
        if (!(key instanceof String)) {
            throw new IllegalArgumentException("Key must be a string.");
        }

        var lcKey = ((String) key).toLowerCase();
        var originalKey = originalKeys.get(lcKey);
        var found = super.remove(originalKey, value);
        if (found) {
            originalKeys.remove(lcKey);
        }
        return found;
    }

    public String getOriginalKey(String key) {
        return originalKeys.get(key);
    }
}

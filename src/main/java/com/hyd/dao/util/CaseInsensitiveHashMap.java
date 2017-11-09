package com.hyd.dao.util;

import java.util.HashMap;

/**
 * 对 key 忽略大小写的 HashMap
 */
public class CaseInsensitiveHashMap<V> extends HashMap<String, V> {

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
        return super.get(key.toLowerCase());
    }

    /**
     * 设置值
     *
     * @param key   键
     * @param value 值
     *
     * @throws IllegalArgumentException 如果 key 不是一个字符串
     */
    @SuppressWarnings({"unchecked"})
    public V put(String key, V value) {
        key = key.toLowerCase();
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

        key = ((String) key).toLowerCase();
        return super.containsKey(key);
    }
}

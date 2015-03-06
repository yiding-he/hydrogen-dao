package com.hyd.dao.util;

import java.util.HashMap;

/**
 * 对 key 忽略大小写的 HashMap
 */
public class CaseInsensitiveHashMap<K, V> extends HashMap<K, V> {

    /**
     * 根据 key 获取值
     *
     * @param key 键
     *
     * @return 相对应的值
     *
     * @throws IllegalArgumentException 如果 key 不是一个字符串
     */
    public V get(Object key) {
        if (!(key instanceof String)) {
            throw new IllegalArgumentException("参数必须是字符串。");
        }
        String strkey = ((String) key).toLowerCase();
        return super.get(strkey);
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
    public V put(K key, V value) {
        if (!(key instanceof String)) {
            throw new IllegalArgumentException("参数必须是字符串。");
        }
        key = (K) ((String) key).toLowerCase();
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
            throw new IllegalArgumentException("参数必须是字符串。");
        }

        key = ((String) key).toLowerCase();
        return super.containsKey(key);
    }
}

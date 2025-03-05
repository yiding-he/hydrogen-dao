package com.hyd.dao.mate.util;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


public class CaseInsensitiveHashMapTest {

    @Test
    public void getSet() throws Exception {
        CaseInsensitiveHashMap<String> map = new CaseInsensitiveHashMap<>();

        map.put("1", "2");
        assertEquals("2", map.get("1"));

        assertNull(map.get("%*&%$%^%^#%^$#^$%#^%$#^%$#^"));

        map.put("AAAaaa", "BBBbbb");
        assertEquals("BBBbbb", map.get("AAAAAA"));
        assertEquals("BBBbbb", map.get("aaaAAA"));

        assertTrue(map.containsKey("AAAaaa"));
        assertTrue(map.containsKey("AAAAAA"));
        assertTrue(map.containsKey("aaaaaa"));
        assertFalse(map.containsKey("bbbbbb"));

        // keySet 只能返回原始 key
        assertTrue(map.keySet().contains("AAAaaa"));
        assertFalse(map.keySet().contains("aaaaaa"));

        // entrySet 只能返回原始 key
        assertTrue(map.entrySet().stream().anyMatch(e -> e.getKey().equals("AAAaaa")));
        assertTrue(map.entrySet().stream().noneMatch(e -> e.getKey().equals("aaaaaa")));

        map.putAll(Map.of("AAAaaa", "CCCCCC"));
        assertEquals("CCCCCC", map.get("AAAAAA"));
        assertEquals("CCCCCC", map.get("AAAaaa"));

    }
}

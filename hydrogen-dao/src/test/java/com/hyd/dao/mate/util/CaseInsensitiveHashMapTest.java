package com.hyd.dao.mate.util;

import org.junit.Test;

import static org.junit.Assert.*;

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

        assertTrue(map.keySet().contains("AAAaaa"));
        assertTrue(map.keySet().contains("aaaaaa"));

        map.put("aaaAAA", "CCCCCC");
        assertEquals("CCCCCC", map.get("AAAAAA"));
        assertEquals("CCCCCC", map.get("AAAaaa"));
    }
}

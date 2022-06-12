package com.hyd.dao.mate.util;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BeanUtilTest {

    @Test
    public void sort() {
        Map<String, String> map1 = new HashMap<>();
        Map<String, String> map2 = new HashMap<>();
        Map<String, String> map3 = new HashMap<>();

        map1.put("name", "user3");
        map2.put("name", "user1");
        map3.put("name", "user2");

        var maps = Arrays.asList(map1, map2, map3);
        BeanUtil.sort(maps, "name", "");
        assertEquals("user1", maps.get(0).get("name"));
        assertEquals("user2", maps.get(1).get("name"));
        assertEquals("user3", maps.get(2).get("name"));
    }
}

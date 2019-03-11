package com.hyd.dao.util;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class BeanUtilTest {

    @Test
    public void sort() {
        Map<String, String> map1 = new HashMap<>();
        Map<String, String> map2 = new HashMap<>();
        Map<String, String> map3 = new HashMap<>();

        map1.put("name", "user3");
        map2.put("name", "user1");
        map3.put("name", "user2");

        Map[] maps = {map1, map2, map3};
        BeanUtil.sort(maps, "name", "");
        for (Map map : maps) {
            System.out.println(map);
        }
    }
}
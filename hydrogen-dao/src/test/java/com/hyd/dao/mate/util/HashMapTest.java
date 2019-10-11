package com.hyd.dao.mate.util;

import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertFalse;

public class HashMapTest {

    @Test
    public void removeNull() throws Exception {
        HashMap<String, String> map = new HashMap<>();
        map.put("1", "2");
        map.remove(null);
        assertFalse(map.isEmpty());
    }
}

package com.hyd.dao.util;

import static org.junit.Assert.assertFalse;

import java.util.HashMap;
import org.junit.Test;

public class HashMapTest {

    @Test
    public void removeNull() throws Exception {
        HashMap<String, String> map = new HashMap<>();
        map.put("1", "2");
        map.remove(null);
        assertFalse(map.isEmpty());
    }
}

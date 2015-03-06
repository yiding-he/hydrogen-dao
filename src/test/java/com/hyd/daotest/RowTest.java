package com.hyd.daotest;

import com.hyd.dao.Row;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertTrue;

/**
 */
public class RowTest {

    @Test
    public void testIsMap() throws Exception {
        assertTrue(new Row() instanceof Map);
    }
}

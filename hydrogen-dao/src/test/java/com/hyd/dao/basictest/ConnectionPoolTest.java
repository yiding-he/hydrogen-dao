package com.hyd.dao.basictest;

import com.hyd.daotests.JUnitRuleTestBase;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ConnectionPoolTest extends JUnitRuleTestBase {

    @Test
    public void testConnectionReleased() throws Exception {
        assertEquals(0, dataSource.getNumActive());
    }
}

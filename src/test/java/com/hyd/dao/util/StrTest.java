package com.hyd.dao.util;

import org.junit.Assert;
import org.junit.Test;

public class StrTest {

    @Test
    public void testColumnToProperty() throws Exception {
        System.out.println(Str.columnToProperty("_my_member_id"));
    }

    @Test
    public void testCount() throws Exception {
        Assert.assertEquals(0, Str.count("", "BC"));
        Assert.assertEquals(1, Str.count("A((BC)ABC1)23", "\\(.*\\)"));
        Assert.assertEquals(2, Str.count("ABCABC123", "BC"));
        Assert.assertEquals(2, Str.count("ABCABC123", "BC?"));
        Assert.assertEquals(3, Str.count("ABCABC12BC3", "BC"));
    }
}
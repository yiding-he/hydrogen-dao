package com.hyd.dao.util;

import org.junit.Test;

import static org.junit.Assert.*;

public class StringUtilTest {

    @Test
    public void testColumnToProperty() throws Exception {
        System.out.println(StringUtil.columnToProperty("_my_member_id"));
    }

    @Test
    public void testPropertyToColumn() throws Exception {

    }
}
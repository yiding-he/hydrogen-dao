package com.hyd.dao.mate.util;

import com.hyd.dao.src.models.Blog;
import org.junit.Assert;
import org.junit.Test;

public class ClsTest  {

    @Test
    public void testHasField() throws Exception {
        Assert.assertTrue(Cls.hasField(Blog.class, "id"));
        Assert.assertTrue(Cls.hasField(Blog.class, "title"));
    }
}

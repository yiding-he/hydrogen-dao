package com.hyd.dao.mate.util;

import com.hyd.dao.src.models.Blog;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ClsTest  {

    @Test
    public void testHasField() throws Exception {
        assertTrue(Cls.hasField(Blog.class, "id"));
        assertTrue(Cls.hasField(Blog.class, "title"));
    }
}

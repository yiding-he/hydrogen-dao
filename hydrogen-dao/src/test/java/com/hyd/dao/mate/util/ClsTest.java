package com.hyd.dao.mate.util;

import com.hyd.dao.src.models.Blog;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.junit.Assert;
import org.junit.Test;

public class ClsTest  {

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class ChildBlog extends Blog {

        private Long parentBlogId;
    }

    @Test
    public void testHasField() throws Exception {
        Assert.assertTrue(Cls.hasField(Blog.class, "id"));
        Assert.assertTrue(Cls.hasField(Blog.class, "title"));
        Assert.assertTrue(Cls.hasField(ChildBlog.class, "id"));
        Assert.assertTrue(Cls.hasField(ChildBlog.class, "title"));
        Assert.assertTrue(Cls.hasField(ChildBlog.class, "parentBlogId"));
    }
}

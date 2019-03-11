package com.hyd.dao.basictest;

import com.hyd.dao.DAO;
import com.hyd.dao.src.models.Blog;
import com.hyd.daotests.JUnitRuleTestBase;
import org.junit.Test;

import java.util.List;
import java.util.function.Supplier;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public class DaoNoNameConvertTest extends JUnitRuleTestBase {

    @Override
    protected Supplier<DAO> getDAOSupplier() {
        return () -> dao2;
    }

    @Test
    public void queryObject() throws Exception {
        List<Blog> blogs = dao2.query(Blog.class, "select * from blog2");
        assertFalse(blogs.isEmpty());
        assertNotNull(blogs.get(0).getCreateTime());
        System.out.println(blogs.get(0).getCreateTime());
    }
}

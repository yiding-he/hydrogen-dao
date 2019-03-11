package com.hyd.dao.basictest;

import com.hyd.dao.DAO;
import com.hyd.dao.Row;
import com.hyd.dao.src.models.Blog;
import com.hyd.daotests.JUnitRuleTestBase;
import org.junit.Test;

import java.util.List;
import java.util.function.Supplier;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public class DaoTest extends JUnitRuleTestBase {

    @Override
    protected Supplier<DAO> getDAOSupplier() {
        return () -> dao;
    }

    @Test
    public void query() throws Exception {
        List<Row> rows = dao.query("select * from blog");
        assertFalse(rows.isEmpty());
    }

    @Test
    public void queryObject() throws Exception {
        List<Blog> blogs = dao.query(Blog.class, "select * from blog");
        assertFalse(blogs.isEmpty());

        assertNotNull(blogs.get(0).getId());
        assertNotNull(blogs.get(0).getContent());
        assertNotNull(blogs.get(0).getCreateTime());
        assertNotNull(blogs.get(0).getTitle());
    }
}

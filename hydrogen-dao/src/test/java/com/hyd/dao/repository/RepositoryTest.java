package com.hyd.dao.repository;

import com.hyd.dao.DAO;
import com.hyd.dao.src.models.Blog;
import com.hyd.daotests.JUnitRuleTestBase;
import org.junit.Test;

import java.util.function.Supplier;

import static org.junit.Assert.*;

public class RepositoryTest extends JUnitRuleTestBase {

    @Override
    protected Supplier<DAO> getDAOSupplier() {
        return () -> dao;
    }

    @Test
    public void testCreateRepository() throws Exception {
        Repository<Blog> repository = new Repository<>(Blog.class, dao, "blog");
    }

    @Test
    public void testFindBlog() throws Exception {
        Repository<Blog> repository = new Repository<>(Blog.class, dao, "blog");
        Blog blog1 = repository.findById(1);
        assertNotNull(blog1);
    }
}

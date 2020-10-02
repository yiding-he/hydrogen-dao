package com.hyd.dao.repository;

import com.hyd.dao.DAO;
import com.hyd.dao.src.models.Blog;
import com.hyd.daotests.JUnitRuleTestBase;
import org.junit.Test;

import java.util.List;
import java.util.function.Supplier;

import static org.junit.Assert.*;

public class RepositoryTest extends JUnitRuleTestBase {

    @Override
    protected Supplier<DAO> getDAOSupplier() {
        return () -> dao;
    }

    private Repository<Blog> getRepository() {
        return new Repository<>(Blog.class, dao, "blog");
    }

    @Test
    public void testCreateRepository() throws Exception {
        Repository<Blog> repository = getRepository();
    }

    @Test
    public void testFindBlog() throws Exception {
        Repository<Blog> repository = getRepository();
        Blog blog1 = repository.findById(1);
        assertNotNull(blog1);
    }

    @Test
    public void testQueryByNullInstance() throws Exception {
        Repository<Blog> repository = getRepository();
        List<Blog> blogs = repository.queryByInstance(null);
        assertFalse(blogs.isEmpty());
        assertEquals(3, blogs.size());
    }

    @Test
    public void testQueryByEmptyInstance() throws Exception {
        Repository<Blog> repository = getRepository();
        List<Blog> blogs = repository.queryByInstance(new Blog());
        assertFalse(blogs.isEmpty());
        assertEquals(3, blogs.size());
    }

    @Test
    public void testQueryByInstance() throws Exception {
        Repository<Blog> repository = getRepository();
        Blog blog;
        List<Blog> blogs;

        blog = new Blog();
        blog.setId(1L);
        blogs = repository.queryByInstance(blog);
        assertFalse(blogs.isEmpty());
        assertEquals(1, blogs.size());

        blog = new Blog();
        blog.setTitle("blog1");
        blogs = repository.queryByInstance(blog);
        assertFalse(blogs.isEmpty());
        assertEquals(1, blogs.size());
    }
}

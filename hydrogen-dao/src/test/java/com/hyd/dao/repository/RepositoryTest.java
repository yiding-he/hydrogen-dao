package com.hyd.dao.repository;

import com.hyd.dao.src.models.Blog;
import com.hyd.daotests.DataSourceFactories;
import com.hyd.daotests.JUnitRuleTestBase;
import org.junit.Test;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

public class RepositoryTest extends JUnitRuleTestBase {

    private Repository<Blog> getRepository() {
        return new Repository<>(Blog.class, getDao(), "blog");
    }

    @Test
    public void testCreateRepository() throws Exception {
        Repository<Blog> repository = getRepository();
    }

    @Test
    public void testQueryById() throws Exception {
        Repository<Blog> repository = getRepository();
        assertNotNull(repository.queryById(1));
        assertNull(repository.queryById(-1));
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

    @Test
    public void testQueryByInstanceMultiField() throws Exception {
        Blog blog = new Blog();
        blog.setId(1L);
        blog.setTitle("blog1");

        Repository<Blog> repository = getRepository();
        List<Blog> blogs = repository.queryByInstance(blog);
        assertFalse(blogs.isEmpty());
        assertEquals(1, blogs.size());
    }

    @Test
    public void testDeleteById() throws Exception {
        Repository<Blog> repository = getRepository();

        assertNotNull(repository.queryById(1));
        repository.deleteById(1);
        assertNull(repository.queryById(1));
    }

    @Test
    public void testDeleteByNullInstance() throws Exception {
        Repository<Blog> repository = getRepository();

        assertEquals(0, repository.deleteByInstance(null)); // no record deleted

        List<Blog> blogs = repository.queryByInstance(null);
        assertFalse(blogs.isEmpty());
        assertEquals(3, blogs.size());
    }

    @Test
    public void testDeleteByEmptyInstance() throws Exception {
        try {
            Repository<Blog> repository = getRepository();

            Blog blog = new Blog();
            assertEquals(0, repository.deleteByInstance(blog)); // no record deleted

            fail("should throw exception");
        } catch (IllegalStateException e) {
            assertTrue(e.getMessage().contains("dangerous operation prohibited"));
        }
    }

    @Test
    public void testDeleteByInstance() throws Exception {
        Repository<Blog> repository = getRepository();
        Blog blog = new Blog();
        blog.setTitle("blog1");

        assertNotNull(repository.queryById(1L));
        repository.deleteByInstance(blog);
        assertNull(repository.queryById(1L));
        assertNotNull(repository.queryById(2L));
    }

    @Test
    public void testDeleteByInstanceMultiField() throws Exception {
        Repository<Blog> repository = getRepository();
        Blog blog = new Blog();
        blog.setTitle("blog1");
        blog.setId(1L);

        assertNotNull(repository.queryById(1L));
        repository.deleteByInstance(blog);
        assertNull(repository.queryById(1L));
        assertNotNull(repository.queryById(2L));
    }

    @Test
    public void testInsertInstance() throws Exception {
        Blog blog = new Blog();
        blog.setId(4L);
        blog.setTitle("blog4");
        blog.setContent("content of blog 4");

        Repository<Blog> repository = getRepository();
        int count = repository.insertInstance(blog);
        assertEquals(1, count);

        Blog _blog = repository.queryById(4);
        assertEquals(Long.valueOf(4), _blog.getId());
        assertEquals("blog4", _blog.getTitle());
    }

    @Test
    public void testInsertBatch() throws Exception {
        List<Blog> blogs = Arrays.asList(
            new Blog(4L, "blog4", "content of blog4", new Date()),
            new Blog(5L, "blog5", "content of blog5", new Date()),
            new Blog(6L, "blog6", "content of blog6", new Date())
        );

        Repository<Blog> repository = getRepository();
        repository.insertBatch(blogs);

        List<Blog> blogList = repository.queryByInstance(null);
        assertEquals(6, blogList.size());
    }

    @Test
    public void testUpdateByEmptyInstance() throws Exception {
        try {
            Blog blog = new Blog();
            getRepository().updateById(blog);
            fail();
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("Update command missing param value for column"));
        }
    }

    @Test
    public void testUpdateByInstance() throws Exception {
        Blog update = new Blog();
        update.setId(1L);
        update.setTitle("Changed Title");

        Repository<Blog> repository = getRepository();
        int count = repository.updateById(update);
        assertEquals(1, count);

        Blog blog = repository.queryById(1L);
        assertEquals("Changed Title", blog.getTitle());
    }

    @Override
    protected DataSource getDataSource() {
        return DataSourceFactories.mysqlDataSource();
    }
}

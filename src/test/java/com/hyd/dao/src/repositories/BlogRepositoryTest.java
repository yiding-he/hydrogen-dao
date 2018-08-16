package com.hyd.dao.src.repositories;

import com.hyd.dao.src.models.Blog;
import com.hyd.daotests.JUnitRuleTestBase;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author yiding.he
 */
public class BlogRepositoryTest extends JUnitRuleTestBase {

    private BlogRepository getRepository() {
        BlogRepository blogRepository = new BlogRepository();
        blogRepository.setDao(dao);
        return blogRepository;
    }

    @Test
    public void testQueryAll() throws Exception {
        List<Blog> blogs = getRepository().queryAll();
        Assert.assertEquals(3, blogs.size());
    }

    @Test
    public void testInsertBlog() throws Exception {
        Blog blog = new Blog();
        blog.setId(999);
        blog.setTitle("ttttt");
        blog.setContent("dfqwliufqwiuefhqliuwefhuiqweyfiw");
        blog.setLastUpdate(new Date());
        blog.setHidden(true);

        getRepository().insert(blog);

        Blog b = getRepository().queryById(999);
        assertNotNull(b);
        assertEquals(blog, b);
    }

    @Test
    public void testInsertBatch() {
        dao.execute("delete from blog");

        List<Blog> blogList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            int id = 500 + i;
            blogList.add(new Blog(id, "blog-" + id, "content-" + id, id % 2 == 0));
        }

        getRepository().insert(blogList);
        List<Blog> blogs = getRepository().queryAll();
        assertEquals(10, blogs.size());
        assertEquals("blog-500", blogs.get(0).getTitle());
    }

    @Test
    public void testQueryNotFound() throws Exception {
        Blog blog = getRepository().queryById(5);
        assertNull(blog);
    }
}

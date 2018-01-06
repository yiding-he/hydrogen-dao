package com.hyd.daotests.tests;

import com.hyd.daotests.Blog;
import com.hyd.daotests.DAOBaseTest;
import com.hyd.dao.Row;
import org.junit.Test;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author yidin
 */
public class TestSingleInsertAndQuery extends DAOBaseTest {

    private int getBlogCount() {
        return dao.count("select count(*) from blog");
    }

    @Test
    public void testInsertAndQuery() {
        assertEquals(0, getBlogCount());

        ///////////////////////////////////////////////

        int blogId = 1;
        String blogTitle = "blog1";
        String blogContent = "content1";
        Date blogCreateTime = new Date();

        dao.execute("insert into blog " +
                        "set id=?, title=?, content=?, create_time=?",
                blogId, blogTitle, blogContent, blogCreateTime);

        assertEquals(1, getBlogCount());

        /////////////////////////////////////////////// test read as row

        List<Row> rows = dao.query("select * from blog");
        assertEquals(1, rows.size());
        assertNotNull(rows.get(0));

        Row row = rows.get(0);
        assertEquals(blogId, row.getIntegerObject("id").intValue());
        assertEquals(blogTitle, row.getString("title"));
        assertEquals(blogContent, row.getString("content"));
        assertEquals(blogCreateTime, row.getDate("create_time"));

        /////////////////////////////////////////////// test read as bean

        List<Blog> blogs = dao.query(Blog.class, "select * from blog");
        assertEquals(1, blogs.size());
        assertNotNull(blogs.get(0));

        Blog blog = blogs.get(0);
        assertEquals(blogId, blog.getId());
        assertEquals(blogTitle, blog.getTitle());
        assertEquals(blogContent, blog.getContent());
        assertEquals(blogCreateTime, blog.getCreateTime());

    }

    @Test
    public void testInserObject() {

        assertEquals(0, getBlogCount());

        ///////////////////////////////////////////////

        int blogId = 1;
        String blogTitle = "blog1";
        String blogContent = "content1";
        Date blogCreateTime = new Date();

        Blog blog = new Blog();
        blog.setId(blogId);
        blog.setTitle(blogTitle);
        blog.setContent(blogContent);
        blog.setCreateTime(blogCreateTime);

        dao.insert(blog, "blog");
        assertEquals(1, getBlogCount());

        /////////////////////////////////////////////// test read as row

        List<Blog> blogs = dao.query(Blog.class, "select * from blog");
        assertEquals(1, blogs.size());
        assertNotNull(blogs.get(0));

        Blog _blog = blogs.get(0);
        assertEquals(blogId, _blog.getId());
        assertEquals(blogTitle, _blog.getTitle());
        assertEquals(blogContent, _blog.getContent());
        assertEquals(blogCreateTime, _blog.getCreateTime());
    }
}

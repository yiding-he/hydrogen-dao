package com.hyd.dao.basictest;

import com.hyd.dao.Page;
import com.hyd.dao.Row;
import com.hyd.dao.src.models.Blog;
import com.hyd.daotests.JUnitRuleTestBase;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class DaoTest extends JUnitRuleTestBase {

    @Test
    public void query() throws Exception {
        List<Row> rows = dao.query("select * from blog");
        assertFalse(rows.isEmpty());
        rows.forEach(System.out::println);
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

    @Test
    public void testQueryPage() throws Exception {
        Page<Blog> page = dao.queryPage(Blog.class, "select * from blog", 2, 0);
        assertNotNull(page);
        assertFalse(page.isEmpty());
        assertNotNull(page.get(0));
        assertEquals(2, page.getTotalPage());
        assertEquals(3, page.getTotal());
    }

    @Test
    public void testInsertNullContent() throws Exception {
        dao.execute("insert into blog(id,title,content)values(?,?,?)", 666, "no-content", null);
    }

    @Test
    public void queryMap() throws Exception {
        List<Row> rows = dao.query("select * from blog");
        assertFalse(rows.isEmpty());
        assertNotNull(rows.get(0).get("id"));
    }

    @Test
    public void testDelete() {
        assertNotNull(dao.queryFirst("select * from blog where id=?", 1));
        dao.execute("delete from blog where id=?", 1);
        assertNull(dao.queryFirst("select * from blog where id=?", 1));
    }
}

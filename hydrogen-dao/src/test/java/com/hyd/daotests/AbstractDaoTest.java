package com.hyd.daotests;

import com.hyd.dao.*;
import com.hyd.dao.junit.HydrogenDAORule;
import com.hyd.dao.src.models.Blog;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.hyd.dao.DataSources.DEFAULT_DATA_SOURCE_NAME;
import static org.junit.jupiter.api.Assertions.*;

public abstract class AbstractDaoTest {

    protected DAO dao;

    protected HydrogenDAORule rule;

    protected DAO getDao() {
        return dao;
    }

    protected abstract DataSource createDataSource();

    protected abstract void closeDataSource(DataSource dataSource) throws SQLException;

    @BeforeEach
    public void init() {
        if (!DataSources.getInstance().contains(DEFAULT_DATA_SOURCE_NAME)) {
            DataSources.getInstance().setDataSource(DEFAULT_DATA_SOURCE_NAME, createDataSource());
        }
        this.dao = new DAO(DEFAULT_DATA_SOURCE_NAME);
        this.rule = new HydrogenDAORule(this::getDao);
        this.rule.before();
    }

    @AfterEach
    public void fin() {
        this.rule.after();
        DataSources.getInstance().closeAll(this::closeDataSource);
    }

    @Test
    public void query() throws Exception {
        List<Row> rows = getDao().query("select * from blog");
        assertFalse(rows.isEmpty());
        rows.forEach(System.out::println);
    }

    @Test
    public void queryObject() throws Exception {
        List<Blog> blogs = getDao().query(Blog.class, "select * from blog");
        assertFalse(blogs.isEmpty());

        assertNotNull(blogs.get(0).getId());
        assertNotNull(blogs.get(0).getContent());
        assertNotNull(blogs.get(0).getCreateTime());
        assertNotNull(blogs.get(0).getTitle());
    }

    @Test
    public void testQueryPage() throws Exception {
        Page<Blog> page = getDao().queryPage(Blog.class, "select * from blog", 2, 0);
        assertNotNull(page);
        assertFalse(page.isEmpty());
        assertNotNull(page.get(0));
        assertEquals(2, page.getTotalPage());
        assertEquals(3, page.getTotal());
    }

    @Test
    public void testQueryIterator() throws Exception {
        AtomicInteger counter = new AtomicInteger();
        try (var rows = getDao().queryIterator("select * from blog")) {
            rows.forEach(row -> {
                counter.incrementAndGet();
                System.out.println(row);
            });
        }
        assertEquals(3, counter.get());
    }

    @Test
    public void testQueryIteratorBean() {
        AtomicInteger counter = new AtomicInteger();
        try (var rows = getDao().queryIterator("select * from blog")) {
            rows.forEach(Blog.class, blog -> {
                counter.incrementAndGet();
                assertNotNull(blog.getId());
            });
        }
        assertEquals(3, counter.get());
    }

    @Test
    public void testInsertNullContent() throws Exception {
        getDao().execute("insert into blog(id,title,content)values(?,?,?)", 666, "no-content", null);
        Blog blog = getDao().queryFirst(Blog.class, "select * from blog where id=?", 666);
        assertNotNull(blog);
        assertNull(blog.getContent());
    }

    @Test
    public void queryMap() throws Exception {
        List<Row> rows = getDao().query("select * from blog");
        assertFalse(rows.isEmpty());
        assertNotNull(rows.get(0).get("id"));
    }

    @Test
    public void testDelete() {
        assertNotNull(getDao().queryFirst("select * from blog where id=?", 1));
        getDao().execute("delete from blog where id=?", 1);
        assertNull(getDao().queryFirst("select * from blog where id=?", 1));
    }


    @Test
    public void testRunTransactionCommit() throws Exception {
        DAO dao = getDao();
        DAO.runTransaction(() -> {
            assertNotNull(dao.queryFirst("select * from blog where id=?", 1));
            dao.execute("delete from blog where id=?", 1);
        });
        assertNull(dao.queryFirst("select * from blog where id=?", 1));
    }

    @Test
    public void testRunTransactionRollback() throws Exception {
        DAO dao = getDao();
        try {
            DAO.runTransaction(() -> {
                assertNotNull(dao.queryFirst("select * from blog where id=?", 1));
                dao.execute("delete from blog where id=?", 1);
                throw new RuntimeException("FAKE ERROR");
            });
        } catch (TransactionException e) {
            e.printStackTrace();
        } finally {
            assertNotNull(dao.queryFirst("select * from blog where id=?", 1));
        }
    }
}

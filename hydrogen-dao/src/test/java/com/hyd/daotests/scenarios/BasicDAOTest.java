package com.hyd.daotests.scenarios;

import com.hyd.dao.DAO;
import com.hyd.dao.Page;
import com.hyd.dao.Row;
import com.hyd.dao.TransactionException;
import com.hyd.daotests.TestBase;
import com.hyd.daotests.model.Blog;
import com.hyd.daotests.model.BlogRecord;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.hyd.dao.SQL.Select;
import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings({"RedundantThrows", "CallToPrintStackTrace"})
public interface BasicDAOTest extends TestBase {

    @Test
    default void query() throws Exception {
        List<Row> rows = getDao().query("select * from blog");
        assertFalse(rows.isEmpty());
        rows.forEach(System.out::println);
    }

    @Test
    default void querySelect() throws Exception {
        var rows = getDao().query(Select(
            Blog::getId, Blog::getTitle, Blog::getContent
        ).From("blog"));
        assertFalse(rows.isEmpty());
        rows.forEach(System.out::println);
    }

    @Test
    default void queryObject() throws Exception {
        List<Blog> blogs = getDao().query(Blog.class, "select * from blog");
        assertFalse(blogs.isEmpty());

        assertNotNull(blogs.get(0).getId());
        assertNotNull(blogs.get(0).getContent());
        assertNotNull(blogs.get(0).getCreateTime());
        assertNotNull(blogs.get(0).getTitle());
    }

    @Test
    default void testQueryPage() throws Exception {
        Page<Blog> page = getDao().queryPage(Blog.class, "select * from blog", 2, 0);
        assertNotNull(page);
        assertFalse(page.isEmpty());
        assertNotNull(page.get(0));
        assertEquals(2, page.getTotalPage());
        assertEquals(3, page.getTotal());
    }

    @Test
    default void testQueryIterator() throws Exception {
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
    default void testQueryIteratorBean() {
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
    default void testInsertNullContent() throws Exception {
        getDao().execute("insert into blog(id,title,content)values(?,?,?)", 666, "no-content", null);
        Blog blog = getDao().queryFirst(Blog.class, "select * from blog where id=?", 666);
        assertNotNull(blog);
        assertNull(blog.getContent());
    }

    @Test
    default void queryMap() throws Exception {
        List<Row> rows = getDao().query("select * from blog");
        assertFalse(rows.isEmpty());
        assertNotNull(rows.get(0).get("id"));
    }

    @Test
    default void testDelete() {
        assertNotNull(getDao().queryFirst("select * from blog where id=?", 1));
        getDao().execute("delete from blog where id=?", 1);
        assertNull(getDao().queryFirst("select * from blog where id=?", 1));
    }


    @Test
    default void testRunTransactionCommit() throws Exception {
        DAO dao = getDao();
        DAO.runTransaction(() -> {
            assertNotNull(dao.queryFirst("select * from blog where id=?", 1));
            dao.execute("delete from blog where id=?", 1);
        });
        assertNull(dao.queryFirst("select * from blog where id=?", 1));
    }

    @Test
    default void testRunTransactionRollback() throws Exception {
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

    @Test
    default void testQueryRecord() {
        var dao = getDao();
        var record = dao.queryFirst(BlogRecord.class, "select * from blog where id=1");
        assertNotNull(record);
        assertEquals(1L, record.id());
    }
}

package com.hyd.daotests;

import com.hyd.dao.*;
import com.hyd.dao.src.models.Blog;
import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

public abstract class AbstractDaoTest extends JUnitRuleTestBase {

    public static final int POOL_SIZE = 10;

    @Before
    public void init() {
        if (!DataSources.getInstance().contains("default")) {
            DataSources.getInstance().setDataSource("default", getDataSource());
        }
        this.dao = new DAO("default");
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
        getDao().queryIterator("select * from blog").iterate(row -> {
            counter.incrementAndGet();
            System.out.println(row);
        });
        assertEquals(3, counter.get());
    }

    @Test
    public void testInsertNullContent() throws Exception {
        getDao().execute("insert into blog(id,title,content)values(?,?,?)", 666, "no-content", null);
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

    @Test
    public void testConnectionPoolLeak() throws Exception {
        DAO dao = getDao();
        assertEquals(0, ((BasicDataSource) getDataSource()).getNumActive());
        for (int i = 0; i < POOL_SIZE * 3; i++) {
            dao.execute("delete from blog where id < 0");
        }
        assertEquals(0, ((BasicDataSource) getDataSource()).getNumActive());
    }

    @Test
    public void testTransactionPoolLeak() throws Exception {
        DAO dao = getDao();
        assertEquals(0, ((BasicDataSource) getDataSource()).getNumActive());
        for (int i = 0; i < POOL_SIZE * 3; i++) {
            DAO.runTransaction(() -> dao.execute("delete from blog where id < 0"));
        }
        assertEquals(0, ((BasicDataSource) getDataSource()).getNumActive());
    }

    @Test
    public void testMultiThreadTransactionPoolLeak() throws Exception {

        DAO dao = getDao();
        assertEquals(0, ((BasicDataSource) getDataSource()).getNumActive());

        Runnable transaction = () -> {
            dao.execute("delete from blog where id < 0");
            sleep(20);
            dao.execute("delete from blog where id < 0");
            System.out.println("Thread finished : " + Thread.currentThread().getName());
            throw new RuntimeException("TEST ERROR");
        };

        ExecutorService pool = Executors.newFixedThreadPool(POOL_SIZE);
        for (int i = 0; i < POOL_SIZE * 3; i++) {
            pool.execute(() -> {
                try {
                    DAO.runTransaction(transaction);
                } catch (TransactionException e) {
                    System.err.println(e.getMessage());
                }
            });
        }

        pool.shutdown();
        pool.awaitTermination(1, TimeUnit.DAYS);

        assertEquals(0, ((BasicDataSource) getDataSource()).getNumActive());
    }
}

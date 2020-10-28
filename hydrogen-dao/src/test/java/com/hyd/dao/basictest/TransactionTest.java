package com.hyd.dao.basictest;

import com.hyd.dao.DAO;
import com.hyd.dao.TransactionException;
import com.hyd.daotests.JUnitRuleTestBase;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class TransactionTest extends JUnitRuleTestBase {

    @Test
    public void testRunTransactionCommit() throws Exception {
        DAO.runTransaction(() -> {
            assertNotNull(dao.queryFirst("select * from blog where id=?", 1));
            dao.execute("delete from blog where id=?", 1);
        });
        assertNull(dao.queryFirst("select * from blog where id=?", 1));
    }

    @Test
    public void testRunTransactionRollback() throws Exception {
        try {
            DAO.runTransaction(() -> {
                assertNotNull(dao.queryFirst("select * from blog where id=?", 1));
                dao.execute("delete from blog where id=?", 1);
                throw new RuntimeException("SIMULATION ERROR");
            });
        } catch (TransactionException e) {
            e.printStackTrace();
        } finally {
            assertNotNull(dao.queryFirst("select * from blog where id=?", 1));
        }
    }
}

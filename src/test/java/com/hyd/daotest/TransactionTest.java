package com.hyd.daotest;

import com.hyd.dao.DAO;
import com.hyd.dao.TransactionException;
import com.hyd.daotest.bean.User;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * @author yiding.he
 */
public class TransactionTest extends BaseTest {

    @Before
    public void init() {
        final DAO dao = getDAO();
        dao.execute("delete from USERS where ID in (?,?)", 111, 222);
    }

    @Test
    public void testSuccessTransaction() throws Exception {
        final DAO dao = getDAO();
        final User user1 = new User(111L, "user01", "pass01");
        final User user2 = new User(222L, "user02", "pass02");

        DAO.runTransaction(new Runnable() {

            public void run() {
                dao.insert(user1);
                dao.insert(user2);
            }
        });

        assertEquals(2, dao.query("select * from USERS where ID in(?,?)", 111, 222).size());
    }

    @Test
    public void testFailedTransaction() throws Exception {
        final DAO dao = getDAO();
        final User user1 = new User(111L, "user01", "pass01");
        final User user2 = new User(222L, "user02", "pass02");

        try {
            DAO.runTransactionWithException(new Runnable() {

                public void run() {
                    dao.insert(user1);
                    dao.insert(user2);

                    throw new RuntimeException("Transaction aborted.");
                }
            });
        } catch (TransactionException e) {
            e.printStackTrace();
        }

        assertEquals(0, dao.query("select * from USERS where ID in(?,?)", 111, 222).size());
    }
}

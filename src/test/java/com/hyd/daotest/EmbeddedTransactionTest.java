package com.hyd.daotest;

import com.hyd.dao.DAO;
import com.hyd.dao.TransactionException;
import com.hyd.daotest.bean.User;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * @author yiding.he
 */
public class EmbeddedTransactionTest extends BaseTest {

    @Test
    public void testTransaction() throws Exception {
        final DAO dao = getDAO();
        final DAO dao2 = getDAO2();
        final User user1 = new User(dao.next("SEQ_USER_ID"), "trans1", "pass");
        final User user2 = new User(dao.next("SEQ_USER_ID"), "trans2", "pass");

        try {
            DAO.runTransaction(new Runnable() {

                public void run() {
                    dao.insert(user1);

                    DAO.runTransaction(new Runnable() {

                        public void run() {
                            dao2.insert(user2);
                            throw new RuntimeException("rollback user2");
                        }
                    });

                }
            });
        } catch (TransactionException e) {
            e.printStackTrace();
        }

        assertEquals(1, dao.query("select * from USERS where USERNAME=?", "trans1").size());
        assertEquals(0, dao2.query("select * from USERS where USERNAME=?", "trans2").size());
    }
}

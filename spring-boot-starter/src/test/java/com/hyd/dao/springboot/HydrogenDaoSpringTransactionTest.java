package com.hyd.dao.springboot;

import com.hyd.dao.DAO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class HydrogenDaoSpringTransactionTest {

    @Autowired
    private DAO dao;

    @Autowired
    private PlatformTransactionManager platformTransactionManager;

    @Test
    public void testTransaction() throws Exception {

        try {
            TransactionTemplate transactionTemplate = new TransactionTemplate(platformTransactionManager);
            transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                    dao.execute("insert into task set content=?", "should not exists");
                    throw new RuntimeException("Transaction Fail!");
                }
            });
        } catch (Exception e) {
            System.err.println("Transaction successfully failed : " + e);
        }

        assertNull(dao.queryFirst("select * from task where content=?", "should not exists"));
    }
}

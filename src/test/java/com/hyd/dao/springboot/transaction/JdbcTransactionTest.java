package com.hyd.dao.springboot.transaction;

import com.hyd.dao.DAO;
import com.hyd.dao.springboot.SpringBootApplicationBase;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

@EnableTransactionManagement
public class JdbcTransactionTest extends SpringBootApplicationBase {

    @Autowired
    private DataSourceTransactionManager transactionManager;

    @Autowired
    private DAO dao;

    @Test
    public void testTransaction() throws Exception {

        System.out.println(dao.query("show tables"));

        outputUsers();

        try {
            TransactionTemplate template = new TransactionTemplate(transactionManager);
            template.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus status) {
                    dao.execute("insert into blog set id=11111, title='123', content='234'");
                    throw new IllegalStateException();
                }
            });
        } catch (TransactionException e) {
            e.printStackTrace();
        }

        outputUsers();
    }

    private void outputUsers() {
        System.out.println("------------------------------------");
        System.out.println("blogs: ");
        System.out.println(dao.query("select * from blog"));
        System.out.println("------------------------------------");
    }
}

package com.hyd.dao.springboot.init;

import com.hyd.dao.DAO;
import com.hyd.dao.Row;
import com.hyd.dao.log.Logger;
import com.hyd.dao.springboot.SpringBootApplicationBase;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class InitializationTest extends SpringBootApplicationBase {

    private static final Logger LOG = Logger.getLogger(InitializationTest.class);

    @Autowired
    private DAO dao;

    @Test
    public void testInitialization() throws Exception {
        dao.execute("create table if not exists user(id int primary key)");
        List<Row> tables = dao.query("show tables");
        Assert.assertFalse(tables.isEmpty());
        System.out.println("----------------------------");
        tables.forEach(LOG::info);
    }
}

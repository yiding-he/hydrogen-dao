package com.hyd.dao.h2;

import static com.hyd.dao.mate.util.DBCPDataSource.newH2FileDataSource;

import com.hyd.dao.DAO;
import com.hyd.dao.DataSources;
import com.hyd.dao.mate.util.ScriptExecutor;

public class H2FileDBTest {

    public static void main(String[] args) {
        DataSources dataSources = new DataSources();
        dataSources.setDataSource("default", newH2FileDataSource("./target/data/payments", false));

        DAO dao = dataSources.getDAO("default");
        ScriptExecutor.execute("classpath:/h2/init-script.sql", dao);
        System.out.println("Database initialized.");

        dao.execute("insert into payments set id=?,amount=?", System.currentTimeMillis(), 100);
        dao.query(Payment.class, "select * from payments").forEach(System.out::println);
    }
}

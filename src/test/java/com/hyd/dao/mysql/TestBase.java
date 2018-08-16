package com.hyd.dao.mysql;

import com.hyd.dao.DAO;
import com.hyd.dao.DataSources;
import com.hyd.dao.util.DBCPDataSource;
import org.apache.commons.dbcp2.BasicDataSource;

public class TestBase {

    public static DAO initDao() {
        BasicDataSource ds = DBCPDataSource.newMySqlDataSource(
                "localhost", 3306, "test", "root", "root123", true, "UTF-8");

        DataSources dataSources = new DataSources();
        dataSources.setDataSource("ds", ds);

        return dataSources.getDAO("ds");
    }
}

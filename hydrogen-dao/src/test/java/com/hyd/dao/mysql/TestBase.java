package com.hyd.dao.mysql;

import com.hyd.dao.DAO;
import com.hyd.dao.DataSources;
import com.hyd.dao.mate.util.DBCPDataSource;
import org.apache.commons.dbcp2.BasicDataSource;

public class TestBase {

    public static DAO initDao() {
        BasicDataSource ds = DBCPDataSource.newMySqlDataSource(
                "localhost", 3306, "test", "root", "root123", true, "UTF-8");

        DataSources dataSources = DataSources.getInstance();
        dataSources.setDataSource("ds", ds);

        return new DAO("ds");
    }
}

package com.hyd.daotests;

import javax.sql.DataSource;

import static com.hyd.dao.mate.util.DBCPDataSource.newMySqlDataSource;

public class DataSourceFactories {

    public static DataSource mysqlDataSource() {
        return newMySqlDataSource(
            "localhost", 3306, "demo", "root", "root123", true, "utf8"
        );
    }
}

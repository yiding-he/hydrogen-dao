package com.hyd.daotests;

import lombok.Data;

import javax.sql.DataSource;

import static com.hyd.dao.mate.util.DBCPDataSource.newMySqlDataSource;

public class DataSourceFactories {

    @Data
    public static class DsProps {

        public final String url;

        public final String userName;

        public final String password;

    }

    public static final DsProps LOCAL_HOST_3306 = new DsProps(
        "jdbc:mysql://localhost:3306/", "root", "root123");

    public static final DsProps LOCAL_HOST_TIDB = new DsProps(
        "jdbc:mysql://localhost:2446/global_projects_dev", "global_projects_dev", "global_projects_dev");

    public static DataSource getDataSource(DsProps dsProps) {
        return newMySqlDataSource(
            dsProps.url, dsProps.userName, dsProps.password
        );
    }
}

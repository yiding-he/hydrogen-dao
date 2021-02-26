package com.hyd.dao.mate.util;


import org.apache.commons.dbcp2.BasicDataSource;

import javax.sql.DataSource;

/**
 * 创建基于 DBCP 的 DataSource 对象
 * created at 2014/12/26
 *
 * @author Yiding
 */
public final class DBCPDataSource {

    private DBCPDataSource() {

    }

    public static BasicDataSource newDataSource(
            String driverClass, String url, String username, String password) {

        BasicDataSource ds = new BasicDataSource();
        ds.setDriverClassName(driverClass);
        ds.setUrl(url);
        ds.setUsername(username);
        ds.setPassword(password);
        return ds;
    }

    public static BasicDataSource newH2MemDataSource() {
        BasicDataSource ds = new BasicDataSource();
        ds.setDriverClassName("org.h2.Driver");
        ds.setUrl("jdbc:h2:mem:db1");
        return ds;
    }

    public static BasicDataSource newH2FileDataSource(String filePath, boolean onlyIfExists) {
        BasicDataSource ds = new BasicDataSource();
        ds.setDriverClassName("org.h2.Driver");
        ds.setUrl("jdbc:h2:" + filePath + (onlyIfExists ? ";IFEXISTS=TRUE" : ""));
        return ds;
    }

    public static BasicDataSource newOracleDataSource(
            String host, int port, String sid, String username, String password) {

        BasicDataSource ds = new BasicDataSource();
        ds.setDriverClassName("oracle.jdbc.OracleDriver");
        ds.setUrl("jdbc:oracle:thin:@" + host + ":" + port + ":" + sid);
        ds.setUsername(username);
        ds.setPassword(password);

        return ds;
    }

    public static BasicDataSource newMySqlDataSource(
            String url, String username, String password) {

        BasicDataSource ds = new BasicDataSource();
        ds.setDriverClassName("com.mysql.jdbc.Driver");
        ds.setUrl(url);
        ds.setUsername(username);
        ds.setPassword(password);
        return ds;
    }

    public static BasicDataSource newMySqlDataSource(
            String host, int port, String database, String username, String password,
            boolean useUnicode, String charEncoding) {

        BasicDataSource ds = new BasicDataSource();
        ds.setDriverClassName("com.mysql.jdbc.Driver");
        ds.setUrl("jdbc:mysql://" + host + ":" + port + "/" + database +
                "?serverTimezone=UTC&useUnicode=" + useUnicode + "&characterEncoding=" + charEncoding);
        ds.setUsername(username);
        ds.setPassword(password);
        return ds;
    }

    public static BasicDataSource newRemoteHsqldbDataSource(
            String host, int port, String database, String username, String password) {

        BasicDataSource ds = new BasicDataSource();
        ds.setDriverClassName("org.hsqldb.jdbc.JDBCDriver");
        ds.setUrl("jdbc:hsqldb:hsql://" + host + ":" + port + "/" + database);
        ds.setUsername(username);
        ds.setPassword(password);
        return ds;
    }

    public static DataSource newSqlServerDataSource(String host, int port, String database, String username, String password) {
        BasicDataSource ds = new BasicDataSource();
        ds.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        ds.setUrl("jdbc:sqlserver://" + host + ":" + port + ";databaseName=" + database);
        ds.setUsername(username);
        ds.setPassword(password);
        return ds;
    }
}

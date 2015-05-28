package com.hyd.dao.util;

import org.apache.commons.dbcp.BasicDataSource;

/**
 * 创建 Oracle 和 MySQL 的 DataSource 对象
 * created at 2014/12/26
 *
 * @author Yiding
 */
public class DBCPDataSource {

    private DBCPDataSource() {

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
            String host, int port, String database, String username, String password,
            boolean useUnicode, String charEncoding) {

        BasicDataSource ds = new BasicDataSource();
        ds.setDriverClassName("com.mysql.jdbc.Driver");
        ds.setUrl("jdbc:mysql://" + host + ":" + port + "/" + database +
                "?useUnicode=" + useUnicode + "&characterEncoding=" + charEncoding);
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
}
package com.hyd.dao.util;

import org.apache.commons.dbcp.BasicDataSource;

/**
 * 创建 Oracle 和 MySQL 的 DataSource 对象
 * created at 2014/12/26
 *
 * @author Yiding
 */
public class DbcpDataSourceCreator {

    public static BasicDataSource createOracleDataSource(
            String host, int port, String sid, String username, String password) {

        BasicDataSource ds = new BasicDataSource();
        ds.setDriverClassName("oracle.jdbc.OracleDriver");
        ds.setUrl("jdbc:oracle:thin:@" + host + ":" + port + ":" + sid);
        ds.setUsername(username);
        ds.setPassword(password);

        return ds;
    }

    public static BasicDataSource createMySqlDataSource(
            String host, int port, String database, String username, String password,
            boolean useUnicode, String charEncoding) {

        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://" + host + ":" + port + "/" + database +
                "?useUnicode=" + useUnicode + "&characterEncoding=" + charEncoding);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        return dataSource;
    }
}

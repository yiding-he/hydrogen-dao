package com.hyd.dao.database.connection;

import com.hyd.dao.DAOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * 直接连接 JDBC
 *
 * @author yiding.he
 */
public class JdbcConnectionFactory extends ConnectionFactory {

    static final Logger log = LoggerFactory.getLogger(JdbcConnectionFactory.class);

    public JdbcConnectionFactory(String dsName, com.hyd.dao.config.Connection config) {
        super(dsName, config);
    }

    public java.sql.Connection getConnection() throws DAOException {
        com.hyd.dao.config.Connection config = getConfig();

        Connection conn;
        try {
            do {
                conn = getConnectionDirectly(config);
            } while (conn.isClosed());
        } catch (SQLException e) {
            throw new DAOException(e);
        }
        return conn;
    }

    /**
     * 直接从 JDBC 驱动获得数据库连接
     *
     * @param config 连接配置
     *
     * @return 数据库连接
     */
    private Connection getConnectionDirectly(com.hyd.dao.config.Connection config) {
        String driver = config.get("driver");
        String url = config.get("url");
        String username = config.get("username");
        String password = config.get("password");

        try {
            Class.forName(driver);
            log.debug("创建 JDBC 连接(" + url + ")...");
            return DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }
}

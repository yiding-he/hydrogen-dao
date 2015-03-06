package com.hyd.dao.database.connection;

import com.hyd.dao.DAOException;
import org.apache.commons.dbcp.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * DBCP 连接池工厂
 *
 * @author <a href="mailto:yiding.he@gmail.com">yiding_he</a>
 */
public class DbcpConnectionFactory extends ConnectionFactory {

    private BasicDataSource pool;

    static final Logger log = LoggerFactory.getLogger(DbcpConnectionFactory.class);

    /**
     * 构造函数
     *
     * @param dsName 数据源名称
     * @param config 连接配置
     */
    public DbcpConnectionFactory(String dsName, com.hyd.dao.config.Connection config) {
        super(dsName, config);
    }

    public Connection getConnection() throws DAOException {
        com.hyd.dao.config.Connection config = getConfig();

        Connection conn;
        try {
            do {
                conn = getConnectionFromPool(config);
            } while (!checkConn(conn));
        } catch (SQLException e) {
            throw new DAOException(e);
        }
        return conn;
    }

    /**
     * 检查连接是否可用
     *
     * @param conn 要检查的连接
     *
     * @return 如果连接未关闭，则返回true
     *
     * @throws SQLException 如果发生数据库错误
     */
    private boolean checkConn(Connection conn) throws SQLException {
        return !conn.isClosed();
    }

    /**
     * 从连接池获得一个数据库连接
     *
     * @param config 数据库连接配置
     *
     * @return 数据库连接
     */
    private Connection getConnectionFromPool(com.hyd.dao.config.Connection config) {
        if (pool == null) {
            log.debug("Creating pool for \"" + getDsName() + "\"(" + config.get("url") + ")...");
            createPool(config);
        }

        try {
            return pool.getConnection();
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    /**
     * 创建连接池
     *
     * @param config 连接配置
     */
    private void createPool(com.hyd.dao.config.Connection config) {
        String driver = config.get("driver");
        String url = config.get("url");
        String username = config.get("username");
        String password = config.get("password");

        pool = new BasicDataSource();
        pool.setDriverClassName(driver);
        pool.setUrl(url);
        pool.setUsername(username);
        pool.setPassword(password);

        if (config.containsKey("dbcp.initialSize")) {
            pool.setInitialSize(config.getInt("dbcp.initialSize"));
        }

        if (config.containsKey("dbcp.maxActive")) {
            pool.setMaxActive(config.getInt("dbcp.maxActive"));
        }

        if (config.containsKey("dbcp.maxIdle")) {
            pool.setMaxIdle(config.getInt("dbcp.maxIdle"));
        }

        if (config.containsKey("dbcp.minIdle")) {
            pool.setMinIdle(config.getInt("dbcp.minIdle"));
        }

        if (config.containsKey("dbcp.maxWait")) {
            pool.setMaxWait(config.getInt("dbcp.maxWait"));
        }

        if (config.containsKey("dbcp.minEvictableIdleTimeMillis")) {
            pool.setMinEvictableIdleTimeMillis(config.getInt("dbcp.minEvictableIdleTimeMillis"));
        }

        if (config.containsKey("dbcp.maxOpenPreparedStatements")) {
            pool.setMaxOpenPreparedStatements(config.getInt("dbcp.maxOpenPreparedStatements"));
        }

        if (config.containsKey("dbcp.poolPreparedStatements")) {
            pool.setPoolPreparedStatements(config.getBool("dbcp.poolPreparedStatements"));
        }
    }

    public int active() {
        if (pool != null) {
            return pool.getNumActive();
        } else {
            return super.active();
        }
    }

    public int max() {
        if (pool != null) {
            return pool.getMaxActive();
        } else {
            return super.active();
        }
    }
}

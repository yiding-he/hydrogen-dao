package com.hyd.dao.database;

import com.hyd.dao.DAOException;
import com.hyd.dao.database.executor.DefaultExecutor;
import com.hyd.dao.database.executor.Executor;
import com.hyd.dao.log.Logger;
import com.hyd.dao.mate.util.Cls;
import com.hyd.dao.spring.SpringConnectionFactory;
import com.hyd.dao.transaction.TransactionManager;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 构造 Executor 对象的工厂。
 *
 * @author <a href="mailto:yiding.he@gmail.com">yiding_he</a>
 */
public class ExecutorFactory {

    private static final Logger LOG = Logger.getLogger(ExecutorFactory.class);

    private final DataSource dataSource;

    private final String dataSourceName;

    public String getDataSourceName() {
        return dataSourceName;
    }

    /**
     * 构造方法
     *
     * @param dataSource 数据源
     */
    public ExecutorFactory(String dataSourceName, DataSource dataSource) {
        this.dataSourceName = dataSourceName;
        this.dataSource = dataSource;
    }

    /**
     * 构造一个 Executor 对象
     *
     * @param autoCommit 是否自动提交
     *
     * @return 构造出的 Executor 对象
     */
    public Executor getExecutor(boolean autoCommit) {

        try {
            Connection connection = getConnection(autoCommit);
            return new DefaultExecutor(dataSourceName, connection);
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    /**
     * 直接获取一个数据库连接（这是一个不安全的方法！）
     *
     * @param autoCommit 是否自动提交
     *
     * @return 数据库连接
     *
     * @throws SQLException 如果获取数据库连接失败
     */
    public Connection getConnection(boolean autoCommit) throws SQLException {

        if (Cls.exists("org.springframework.jdbc.datasource.DataSourceUtils")) {
            LOG.debug("Getting connection from Spring DataSourceUtils...");
            Connection connection = SpringConnectionFactory.getConnection(this.dataSource);
            if (!connection.getAutoCommit()) {
                TransactionManager.start();
            }
            return connection;
        }

        Connection connection = this.dataSource.getConnection();
        connection.setAutoCommit(autoCommit);
        return connection;
    }
}

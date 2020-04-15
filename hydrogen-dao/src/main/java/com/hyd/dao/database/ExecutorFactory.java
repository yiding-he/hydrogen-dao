package com.hyd.dao.database;

import com.hyd.dao.DAOException;
import com.hyd.dao.database.executor.DefaultExecutor;
import com.hyd.dao.database.executor.ExecutionContext;
import com.hyd.dao.database.executor.Executor;
import com.hyd.dao.database.type.NameConverter;
import com.hyd.dao.log.Logger;
import com.hyd.dao.mate.util.Cls;
import com.hyd.dao.spring.SpringConnectionFactory;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;

/**
 * 构造 Executor 对象的工厂。
 *
 * @author <a href="mailto:yiding.he@gmail.com">yiding_he</a>
 */
public class ExecutorFactory {

    private static final Logger LOG = Logger.getLogger(ExecutorFactory.class);

    private final DataSource dataSource;

    private final String dataSourceName;

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
     * 构造一个 Executor 对象。如果 standalone 为 true，即使当前处于事务当中，这个
     * Executor 对象也会使用新的数据库连接，从而独立于事务执行数据库操作。
     *
     * @param standalone 是否独立于现有事务之外
     *
     * @return 构造出的 Executor 对象
     */
    public Executor getExecutor(boolean standalone) {
        return getExecutor(standalone, false);
    }

    /**
     * 构造一个 Executor 对象。如果 standalone 为 true，即使当前处于事务当中，这个
     * Executor 对象也会使用新的数据库连接，从而独立于事务执行数据库操作。
     *
     * @param standalone 是否独立于现有事务之外
     * @param autoCommit 是否自动提交
     *
     * @return 构造出的 Executor 对象
     */
    public Executor getExecutor(boolean standalone, boolean autoCommit) {

        Executor executor;

        if (TransactionManager.isInTransaction() && !standalone) {
            executor = TransactionManager.getExecutor(this.dataSourceName);
            if (executor == null) {
                executor = createExecutor(false);
                TransactionManager.setExecutor(this.dataSourceName, executor);
            }
        } else {
            executor = createExecutor(autoCommit);
        }

        return executor;
    }

    /**
     * 创建新的 Executor 对象
     *
     * @param autoCommit 是否自动提交，当创建用于事务的 Executor 对象时，可以传 false。
     *
     * @return 新的 Executor 对象
     */
    private Executor createExecutor(boolean autoCommit) {

        try {
            Connection connection = getConnection(autoCommit);

            ExecutionContext context = new ExecutionContext();
            context.setDataSourceName(dataSourceName);
            context.setConnection(connection);
            context.setNameConverter(NameConverter.DEFAULT);

            return new DefaultExecutor(context);
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
            if (connection != null && !connection.getAutoCommit()) {
                TransactionManager.start();
            }
            return connection;
        }

        Connection connection = this.dataSource.getConnection();
        connection.setAutoCommit(autoCommit);
        return connection;
    }
}

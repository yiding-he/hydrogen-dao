package com.hyd.dao.database;

import com.hyd.dao.DAOException;
import com.hyd.dao.database.connection.ConnectionUtil;
import com.hyd.dao.database.executor.DefaultExecutor;
import com.hyd.dao.database.executor.Executor;
import com.hyd.dao.database.executor.MysqlExecutor;
import com.hyd.dao.database.executor.OracleExecutor;
import com.hyd.dao.snapshot.ExecutorInfo;
import com.hyd.dao.snapshot.Snapshot;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 根据配置文件构造 Executor 对象的工厂。Executor 持有 Connection 对象，
 * 是一次性的，Executor 对象执行完后即可丢弃，除非是在事务当中。在事务当中，
 * TransactionManager 将缓存 Executor 对象，作为事务当中执行下一次语句时用。
 *
 * @author <a href="mailto:yiding.he@gmail.com">yiding_he</a>
 */
public class ExecutorFactory {

    private final DataSource dataSource;

    private String dataSourceName;

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
            Executor executor = getExecutorByDatabaseType(connection);
            ExecutorInfo info = new ExecutorInfo(this.dataSourceName, executor);
            Snapshot.getInstance(this.dataSourceName).addExecutorInfo(info);
            executor.setInfo(info);
            return executor;
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    /**
     * 根据数据库类型返回相应的 Executor 子类
     *
     * @param connection 数据库连接
     *
     * @return 相应的 Executor 子类
     *
     * @throws SQLException 如果读取数据库信息失败
     */
    private Executor getExecutorByDatabaseType(Connection connection) throws SQLException {
        Executor executor;
        String type = ConnectionUtil.getDatabaseType(connection);

        if (type.contains("Oracle")) {
            executor = new OracleExecutor(connection);

        } else if (type.contains("MySQL")) {
            executor = new MysqlExecutor(connection);

        } else {
            executor = new DefaultExecutor(connection);
        }

        return executor;
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
        Connection connection = this.dataSource.getConnection();
        connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
        connection.setAutoCommit(autoCommit);
        return connection;
    }
}

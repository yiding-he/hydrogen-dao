package com.hyd.dao.transaction;

import com.hyd.dao.DAO;
import com.hyd.dao.DAOException;
import com.hyd.dao.DataSources;
import com.hyd.dao.database.executor.Executor;
import com.hyd.dao.log.Logger;
import com.hyd.dao.mate.util.Cls;
import com.hyd.dao.mate.util.ConnectionContext;
import com.hyd.dao.spring.SpringConnectionFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 管理事务开始和结束，并缓存事务当中要用到的 Executor 对象。
 * 事务可以分多级，每一级用 Map 保存多个数据源的当前连接。
 * 每一级都需要单独执行 commit/rollback。当执行 commit/rollback
 * 时，会对该级的所有数据源执行。
 * <p>
 * 这个不是分布式事务，无法保证一致性。
 * <p>
 * 关于多级事务：
 * 多级事务会占用多个数据库连接（每一级事务对每个用到的数据源都会占用一个连接），
 * 连接池不够用的情况下可能会造成假死，所以请慎重使用
 */
public class TransactionManager {

    public static final int DEFAULT_ISOLATION_LEVEL = Connection.TRANSACTION_READ_COMMITTED;

    private static final Logger LOG = Logger.getLogger(TransactionManager.class);

    /////////////////////////////////////////////////////////

    /**
     * Executor 缓存，每一层事务都有单独的 datasource-executor mapping
     */
    private static final ThreadLocal<Map<Integer, Map<String, Executor>>>
        executorCache = ThreadLocal.withInitial(ConcurrentHashMap::new);

    private static final ThreadLocal<Map<Integer, Map<String, DataSource>>>
        dataSourceCache = ThreadLocal.withInitial(ConcurrentHashMap::new);

    private static final ThreadLocal<Integer> level = ThreadLocal.withInitial(() -> 0);

    private static final ThreadLocal<Map<Integer, Integer>> isolations = ThreadLocal.withInitial(HashMap::new);

    /////////////////////////////////////////////////////////

    private TransactionManager() {
    }

    /**
     * 判断当前线程是否处于事务当中
     *
     * @return 如果线程处于事务当中，则返回 true
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isInTransaction() {
        return getLevel() > 0;
    }

    /**
     * 获取当前所处的事务级别（最外层事务为 1 级，里层递增）
     *
     * @return 当前所处事务级别。如果当前不处于事务中，则返回 -1
     */
    public static int getLevel() {
        Integer _level = level.get();
        return _level == null ? -1 : _level;
    }

    private static int getIsolation(int level) {
        return isolations.get().getOrDefault(level, DEFAULT_ISOLATION_LEVEL);
    }

    /**
     * 开始一个事务
     */
    public static void start() {
        int _level;

        if (!isInTransaction()) {
            _level = 1;
        } else {
            _level = level.get() + 1;
        }

        LOG.debug("Starting transaction level " + _level);
        level.set(_level);
        executorCache.get().put(_level, new HashMap<>());
    }

    /**
     * 提交当前级别事务
     */
    public static void commit() {
        if (!isInTransaction()) {
            return;
        }

        int _level = level.get();
        Map<String, Executor> executorMap = executorCache.get().get(_level);
        for (Executor executor : executorMap.values()) {
            executor.close();
        }

        LOG.info(() -> "Transaction level " + _level + " commited.");
        level.set(_level - 1);
    }

    /**
     * 回退当前级别事务
     */
    public static void rollback() {
        if (!isInTransaction()) {
            return;
        }

        int _level = level.get();
        Map<String, Executor> executorMap = executorCache.get().get(_level);
        for (Executor executor : executorMap.values()) {
            executor.rollbackAndClose();
        }

        LOG.info(() -> "Transaction level " + _level + " rollbacked.");
        level.set(_level - 1);
    }

    /**
     * 设置 JDBC 事务隔离级别，对所有数据源生效。
     *
     * @param isolation JDBC 事务隔离级别
     */
    public static void setTransactionIsolation(int isolation) {
        if (!isInTransaction()) {
            return;
        }

        int _level = getLevel();
        isolations.get().put(_level, isolation);
    }

    /////////////////////////////////////////////////////////////////

    public static ConnectionContext getConnectionContext(DAO dao) {
        DataSources dataSources = DataSources.getInstance();
        Connection connection;

        try {
            if (dao.isStandAlone() || !isInTransaction()) {
                // 不打算以事务方式执行，或当前没有进行中的事务
                connection = getStandAloneConnection(dao, dataSources);
            } else {
                // 获取当前事务中缓存的 Connection，如果没有则自动新建一个
                connection = getInTransactionConnection(dao, dataSources);
            }
        } catch (SQLException e) {
            throw new DAOException(e);
        }

        return new ConnectionContext(dao.getDataSourceName(), connection, dao.getNameConverter());
    }

    @SuppressWarnings("MagicConstant")
    private static Connection getInTransactionConnection(DAO dao, DataSources dataSources)  {
        try {
            int level = getLevel();
            int isolation = getIsolation(level);
            String dataSourceName = dao.getDataSourceName();

            DataSource dataSource = dataSourceCache.get()
                .computeIfAbsent(level, __ -> new ConcurrentHashMap<>())
                .computeIfAbsent(dataSourceName, dataSources::getDataSource);

            Connection connection;

            if (Cls.exists("org.springframework.jdbc.datasource.DataSourceUtils")) {
                LOG.debug("Getting connection from Spring DataSourceUtils...");
                connection = SpringConnectionFactory.getConnection(dataSource);
                if (!connection.getAutoCommit()) {
                    TransactionManager.start();
                }
            } else {
                connection = dataSource.getConnection();
                connection.setTransactionIsolation(isolation);
            }

            return connection;
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    private static Connection getStandAloneConnection(DAO dao, DataSources dataSources) throws SQLException {
        String dataSourceName = dao.getDataSourceName();
        DataSource dataSource = dataSources.getDataSource(dataSourceName);
        Connection connection = dataSource.getConnection();
        connection.setAutoCommit(true);
        return connection;
    }
}

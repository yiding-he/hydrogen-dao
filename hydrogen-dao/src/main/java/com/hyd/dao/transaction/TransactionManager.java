package com.hyd.dao.transaction;

import com.hyd.dao.DAO;
import com.hyd.dao.DAOException;
import com.hyd.dao.DataSources;
import com.hyd.dao.database.ConnectionContext;
import com.hyd.dao.database.ConnectionHolder;
import com.hyd.dao.log.Logger;
import com.hyd.dao.mate.util.Cls;
import com.hyd.dao.spring.SpringConnectionFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 管理事务开始和结束，并缓存事务当中要用到的 Executor 对象。
 * 事务可以分多级，每一级用 Map 保存多个数据源的当前连接。
 * 每一级都需要单独执行 commit/rollback。当执行 commit/rollback
 * 时，会对该级的所有数据源执行。
 * <ul>
 *   <li>这个不是分布式事务，无法保证一致性。</li>
 * <ul>
 * 关于多级事务：
 * 多级事务会占用多个数据库连接（每一级事务对每个用到的数据源都会占用一个连接），
 * 连接池不够用的情况下可能会造成假死，所以请慎重使用
 * 如果检测到处于 Spring JDBC 事务当中，会禁用多级事务，完全从 Spring 获取连接
 */
public class TransactionManager {

    public static final int DEFAULT_ISOLATION_LEVEL = Connection.TRANSACTION_READ_COMMITTED;

    private static final Logger LOG = Logger.getLogger(TransactionManager.class);

    private static final ThreadLocal<Map<Integer, Map<String, ConnectionContext>>>
        connectionContextCache = ThreadLocal.withInitial(ConcurrentHashMap::new);

    private static final ThreadLocal<Map<Integer, Map<String, DataSource>>>
        dataSourceCache = ThreadLocal.withInitial(ConcurrentHashMap::new);

    private static final ThreadLocal<Map<Integer, Integer>> isolations = ThreadLocal.withInitial(HashMap::new);

    private static final ThreadLocal<Integer> level = ThreadLocal.withInitial(() -> 0);

    private static boolean isInSpringTransaction() {
        return Cls.exists("org.springframework.jdbc.datasource.DataSourceUtils") && SpringConnectionFactory.isTransactionActive();
    }

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
        if (isInSpringTransaction()) {
            return true;
        }

        return getLevel() > 0;
    }

    /**
     * 获取当前所处的事务级别（最外层事务为 1 级，里层递增）
     *
     * @return 当前所处事务级别。如果当前不处于事务中，则返回 0
     */
    public static int getLevel() {
        Integer _level = level.get();
        return _level == null ? 0 : _level;
    }

    private static int getIsolation(int level) {
        return isolations.get().getOrDefault(level, DEFAULT_ISOLATION_LEVEL);
    }

    /**
     * 开始一个事务
     */
    public static void start() {
        int level;

        if (!isInTransaction() || isInSpringTransaction()) {
            level = 1;
        } else {
            level = getLevel() + 1;
        }

        LOG.debug("Starting transaction level " + level);
        TransactionManager.level.set(level);
    }

    /**
     * 提交当前级别事务
     */
    public static void commit() {
        int level = getLevel();
        if (!isInTransaction() || isInSpringTransaction()) {
            TransactionManager.level.set(Math.max(0, level - 1));
            return;
        }

        connectionContextCache.get()
            .getOrDefault(level, Collections.emptyMap())
            .forEach((dataSourceName, context) -> context.commit());

        connectionContextCache.get().remove(level);

        LOG.info(() -> "Transaction level " + level + " committed.");
        TransactionManager.level.set(Math.max(0, level - 1));
    }

    /**
     * 回退当前级别事务
     */
    public static void rollback() {
        int level = getLevel();
        if (!isInTransaction() || isInSpringTransaction()) {
            TransactionManager.level.set(Math.max(0, level - 1));
            return;
        }

        connectionContextCache.get()
            .getOrDefault(level, Collections.emptyMap())
            .forEach((dataSourceName, context) -> context.rollback());

        connectionContextCache.get().remove(level);

        LOG.info(() -> "Transaction level " + level + " rollbacked.");
        TransactionManager.level.set(Math.max(0, level - 1));
    }

    /**
     * 设置 JDBC 事务隔离级别，对所有数据源生效。
     *
     * @param isolation JDBC 事务隔离级别
     */
    public static void setTransactionIsolation(int isolation) {
        if (!isInTransaction() || isInSpringTransaction()) {
            return;
        }

        isolations.get().put(getLevel(), isolation);
    }

    /////////////////////////////////////////////////////////////////

    /**
     * 获取一个适合上下文的 ConnectionContext 对象，用完之后必须调用 {@link ConnectionContext#closeIfAutoCommit()} 方法。
     */
    public static ConnectionContext getConnectionContext(DAO dao) {
        // 如果要求独立于事务之外，则直接返回不妨到缓存
        if (dao.isStandAlone() || !isInTransaction() || isInSpringTransaction()) {
            return createConnectionContext(dao);
        }

        // 否则优先从缓存获取
        return connectionContextCache.get()
            .computeIfAbsent(getLevel(), any -> new ConcurrentHashMap<>())
            .computeIfAbsent(dao.getDataSourceName(), any -> createConnectionContext(dao));
    }

    private static ConnectionContext createConnectionContext(DAO dao) {
        return ConnectionContext.create(
            dao.getDataSourceName(), connectionHolder(dao), dao.getNameConverter()
        );
    }

    private static ConnectionHolder connectionHolder(DAO dao) {
        return ConnectionHolder.fromSupplier(() -> {
            DataSources dataSources = DataSources.getInstance();
            Connection connection;

            if (dao.isStandAlone() || !isInTransaction()) {
                // 不打算以事务方式执行，或当前没有进行中的事务
                connection = getStandAloneConnection(dao, dataSources);
            } else {
                // 获取当前事务中缓存的 Connection，如果没有则自动新建一个
                connection = getInTransactionConnection(dao, dataSources);
            }

            return connection;
        });
    }

    @SuppressWarnings("MagicConstant")
    private static Connection getInTransactionConnection(DAO dao, DataSources dataSources) {
        try {
            int level = getLevel();
            int isolation = getIsolation(level);
            String dataSourceName = dao.getDataSourceName();

            DataSource dataSource = dataSourceCache.get()
                .computeIfAbsent(level, any -> new ConcurrentHashMap<>())
                .computeIfAbsent(dataSourceName, dataSources::getDataSource);

            Connection connection;

            if (Cls.exists("org.springframework.jdbc.datasource.DataSourceUtils")) {
                LOG.debug("Getting connection from Spring DataSourceUtils...");
                connection = SpringConnectionFactory.getConnection(dataSource);
            } else {
                connection = dataSource.getConnection();
                connection.setTransactionIsolation(isolation);
            }

            connection.setAutoCommit(false);
            return connection;
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    private static Connection getStandAloneConnection(DAO dao, DataSources dataSources) {
        try {
            String dataSourceName = dao.getDataSourceName();
            DataSource dataSource = dataSources.getDataSource(dataSourceName);
            Connection connection = dataSource.getConnection();
            connection.setAutoCommit(true);
            return connection;
        } catch (Exception e) {
            throw new DAOException("Error getting connection, dataSourceName=" + dao.getDataSourceName(), e);
        }
    }
}

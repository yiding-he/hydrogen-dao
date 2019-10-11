package com.hyd.dao.database;

import com.hyd.dao.TransactionException;
import com.hyd.dao.database.executor.Executor;
import com.hyd.dao.log.Logger;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * 管理事务开始和结束，并缓存事务当中要用到的 Executor 对象
 *
 * 关于多级事务：
 * 多级事务会占用大量连接（每个线程的每一级事务中对每个数据源都会占用一个连接），
 * 连接池不够用的情况下可能会造成假死，所以请慎重使用
 */
public class TransactionManager {

    public static final int DEFAULT_ISOLATION_LEVEL = Connection.TRANSACTION_READ_COMMITTED;

    private static final Logger LOG = Logger.getLogger(TransactionManager.class);

    /////////////////////////////////////////////////////////

    private TransactionManager() {
        // hide public constructor
    }

    /**
     * Executor 缓存，每一层事务都有单独的 datasource-executor mapping
     */
    private static ThreadLocal<Map<Integer, Map<String, Executor>>>
            executorCache = new ThreadLocal<>();

    private static ThreadLocal<Integer> level = ThreadLocal.withInitial(() -> 0);

    private static ThreadLocal<Map<Integer, Integer>> isolations = ThreadLocal.withInitial(HashMap::new);

    /**
     * 判断当前线程是否处于事务当中
     *
     * @return 如果线程处于事务当中，则返回 true
     */
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

    /**
     * 获得处于当前事务中的 Executor 对象
     *
     * @param dsName 数据源名称
     *
     * @return 处于当前事务中的 Executor 对象。如果当前不处于事务中，则返回 null；即使处于事务中，也有可能返回 null。
     */
    public static Executor getExecutor(String dsName) {
        if (!isInTransaction()) {
            return null;
        }

        int level = getLevel();
        Map<String, Executor> executors = executorCache.get().get(level);
        return executors.get(dsName + ":" + level);
    }

    /**
     * 缓存当前事务的 Executor 对象
     *
     * @param dsName   数据源名称
     * @param executor Executor 对象
     */
    public static void setExecutor(String dsName, Executor executor) {
        if (!isInTransaction()) {
            return;
        }

        try {
            int level = getLevel();
            executor.setTransactionIsolation(getIsolation(level));
            Map<String, Executor> executors = executorCache.get().get(level);
            executors.put(dsName + ":" + level, executor);
        } catch (SQLException e) {
            throw new TransactionException(e);
        }
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
            executorCache.set(new HashMap<>()); // start level1 transaction
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

}

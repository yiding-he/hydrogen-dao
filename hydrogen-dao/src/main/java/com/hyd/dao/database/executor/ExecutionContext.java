package com.hyd.dao.database.executor;

import com.hyd.dao.database.ExecutorFactory;
import com.hyd.dao.database.type.NameConverter;
import com.hyd.dao.transaction.TransactionManager;

import java.sql.Connection;
import java.util.Objects;

/**
 * 执行本次数据库操作的上下文信息
 */
public class ExecutionContext {

    private static final ThreadLocal<ExecutionContext> HOLDER = new ThreadLocal<>();

    /**
     * 初始化当前线程的 ExecutionContext 对象
     */
    public static Executor init(ExecutorFactory executorFactory, boolean standAlone, NameConverter nameConverter) {
        Executor executor = TransactionManager.getExecutor(executorFactory, standAlone, nameConverter);

        ExecutionContext context = new ExecutionContext();
        context.dataSourceName = executorFactory.getDataSourceName();
        context.connection = executor.getConnection();
        context.nameConverter = nameConverter;

        HOLDER.set(context);
        return executor;
    }

    /**
     * 获取当前线程的 ExecutionContext 对象。如果没有初始化则抛出异常
     *
     * @return 当前线程的 ExecutionContext 对象
     */
    public static ExecutionContext get() {
        ExecutionContext context = HOLDER.get();
        return Objects.requireNonNull(context,
            "Context not exists. Please call ExecutionContext.init() first.");
    }

    /**
     * 清除当前线程的 ExecutionContext 对象
     */
    public static void clear() {
        HOLDER.remove();
    }

    private String dataSourceName;

    private Connection connection;

    private NameConverter nameConverter;

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public Connection getConnection() {
        return connection;
    }

    public String getDataSourceName() {
        return dataSourceName;
    }

    public void setDataSourceName(String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }

    public NameConverter getNameConverter() {
        return nameConverter;
    }

    public void setNameConverter(NameConverter nameConverter) {
        this.nameConverter = nameConverter;
    }
}

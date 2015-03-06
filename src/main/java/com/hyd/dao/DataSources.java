package com.hyd.dao;

import com.hyd.dao.database.ExecutorFactory;
import com.hyd.dao.util.LockFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * 管理数据源配置
 *
 * @author yiding.he
 */
public class DataSources {

    /**
     * 持有 {@link javax.sql.DataSource} 对象的集合
     */
    private Map<String, DataSource> dataSources = new HashMap<String, DataSource>();

    /**
     * “数据源名称 -> ExecutorFactory对象” 映射关系
     */
    private Map<String, ExecutorFactory> executorFactories = new HashMap<String, ExecutorFactory>();

    public Map<String, DataSource> getDataSources() {
        return dataSources;
    }

    public void setDataSources(Map<String, DataSource> dataSources) {
        this.dataSources = dataSources;
    }

    public void setDataSource(String dataSourceName, DataSource dataSource) {
        this.dataSources.put(dataSourceName, dataSource);
    }

    /**
     * 操作数据库连接。使用本方法无需在 ConnectionExecutor 中手动关闭连接。即使出现异常，连接也会关闭。
     *
     * @param dataSourceName     数据源名称
     * @param connectionExecutor 要进行的操作
     *
     * @throws SQLException 如果操作数据库失败
     */
    public void withConnection(String dataSourceName, ConnectionExecutor connectionExecutor) throws SQLException {
        if (!dataSources.containsKey(dataSourceName)) {
            throw new DAOException("Data source '" + dataSourceName + "' not found.");
        }

        Connection connection = dataSources.get(dataSourceName).getConnection();
        try {
            connectionExecutor.execute(connection);
        } finally {
            connection.close();
        }
    }

    /**
     * 根据数据源名称获取或创建一个 DAO 对象
     *
     * @param dsName 数据源名称
     *
     * @return DAO 对象
     */
    public DAO getDAO(String dsName) {
        return getDAO(dsName, false);
    }

    /**
     * 根据数据源名称获取或创建一个 DAO 对象
     *
     * @param dsName     数据源名称
     * @param standAlone 是否独立于当前事务之外
     *
     * @return DAO 对象
     */
    public DAO getDAO(String dsName, boolean standAlone) {
        DAO dao = new DAO(dsName, standAlone);
        dao.setExecutorFactory(getExecutorFactory(dsName));
        return dao;
    }

    /**
     * 根据数据源名称获取或创建一个 DAO 对象
     *
     * @param dsName       数据源名称
     * @param standAlone   是否独立于当前事务之外
     * @param subclassType 用于包装的子类型，该类必须实现构造方法 (String, boolean)。
     *
     * @return DAO 对象
     */
    public <T extends DAO> T getDAO(String dsName, boolean standAlone, Class<T> subclassType) {
        try {
            T dao = subclassType.getConstructor(String.class, Boolean.TYPE).newInstance(dsName, standAlone);
            dao.setExecutorFactory(getExecutorFactory(dsName));
            return dao;
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }

    /**
     * 根据数据源名称获取 ExecutorFactory 对象
     *
     * @param dsName 数据源名称
     *
     * @return ExecutorFactoty 对象
     */
    private ExecutorFactory getExecutorFactory(String dsName) {

        synchronized (LockFactory.getLock("ds:" + dsName)) {
            if (dsName == null) {
                return null;
            }

            if (!dataSources.containsKey(dsName)) {
                throw new IllegalArgumentException("Unknown data source '" + dsName + "'");
            }

            if (executorFactories.containsKey(dsName)) {
                return executorFactories.get(dsName);
            }

            DataSource dataSource = getDataSources().get(dsName);
            ExecutorFactory factory = new ExecutorFactory(dsName, dataSource);

            executorFactories.put(dsName, factory);
            return factory;
        }
    }
}

package com.hyd.dao;

import com.hyd.dao.database.ExecutorFactory;
import com.hyd.dao.database.type.NameConverter;
import com.hyd.dao.mate.util.Locker;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import javax.sql.DataSource;

/**
 * 管理数据源配置
 *
 * @author yiding.he
 */
public class DataSources {

    public static final String DEFAULT_DATA_SOURCE_NAME = "default";

    /**
     * dsName -> DataSource
     */
    private Map<String, DataSource> dataSources = new HashMap<>();

    /**
     * dsName -> ExecutorFactory
     */
    private final Map<String, ExecutorFactory> executorFactories = new HashMap<>();

    /**
     * dsName -> NameConverter
     */
    private final Map<String, NameConverter> columnNameConverters = new HashMap<>();

    /**
     * 删除指定的数据源
     *
     * @param dataSourceName 数据源名称
     * @param finalization   删除后要对数据源做什么操作（例如关闭）
     */
    public void remove(String dataSourceName, Consumer<DataSource> finalization) {
        DataSource dataSource = dataSources.get(dataSourceName);

        if (dataSource != null) {
            dataSources.remove(dataSourceName);
            executorFactories.remove(dataSourceName);
            finalization.accept(dataSource);
        }
    }

    public Map<String, DataSource> getDataSources() {
        return dataSources;
    }

    public void setDataSources(Map<String, DataSource> dataSources) {
        this.dataSources = dataSources;
    }

    public void setDataSource(String dataSourceName, DataSource dataSource) {
        this.dataSources.put(dataSourceName, dataSource);
    }

    public void setColumnNameConverter(String dataSourceName, NameConverter nameConverter) {
        this.columnNameConverters.put(dataSourceName, nameConverter);
    }

    public boolean contains(String dsName) {
        return this.dataSources.containsKey(dsName);
    }

    /**
     * 操作数据库，连接然后自动关闭连接。
     *
     * @param dataSourceName     数据源名称
     * @param connectionConsumer 要进行的操作
     *
     * @throws SQLException 如果操作数据库失败
     */
    public void withConnection(String dataSourceName, Consumer<Connection> connectionConsumer) throws SQLException {

        if (!dataSources.containsKey(dataSourceName)) {
            throw new DAOException("Data source '" + dataSourceName + "' not found.");
        }

        try (Connection connection = dataSources.get(dataSourceName).getConnection()) {
            connectionConsumer.accept(connection);
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

        if (!contains(dsName)) {
            return null;
        }

        DAO dao = new DAO(dsName, standAlone);
        dao.setExecutorFactory(getExecutorFactory(dsName));
        dao.setNameConverter(columnNameConverters.getOrDefault(dsName, NameConverter.DEFAULT));
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

        if (!contains(dsName)) {
            return null;
        }

        try {
            T dao = subclassType.getConstructor(String.class, Boolean.TYPE).newInstance(dsName, standAlone);
            dao.setExecutorFactory(getExecutorFactory(dsName));
            dao.setNameConverter(columnNameConverters.getOrDefault(dsName, NameConverter.DEFAULT));
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
     * @return ExecutorFactory 对象
     */
    private ExecutorFactory getExecutorFactory(String dsName) {

        if (dsName == null) {
            return null;
        }

        return Locker.lockAndRun("ds:" + dsName, () -> {
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
        });
    }

    public boolean isEmpty() {
        return this.dataSources.isEmpty();
    }
}

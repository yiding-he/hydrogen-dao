package com.hyd.dao;

import com.hyd.dao.database.ExecutorFactory;
import com.hyd.dao.database.type.NameConverter;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * 管理数据源配置，允许在运行时动态添加数据源
 *
 * @author yiding.he
 */
public class DataSources {

    private static final DataSources INSTANCE = new DataSources();

    public static final String DEFAULT_DATA_SOURCE_NAME = "default";

    /**
     * dsName -> DataSource
     */
    private Map<String, DataSource> dataSources = new ConcurrentHashMap<>();

    /**
     * dsName -> ExecutorFactory
     */
    private final Map<String, ExecutorFactory> executorFactories = new HashMap<>();

    /**
     * dsName -> NameConverter
     */
    private final Map<String, NameConverter> columnNameConverters = new HashMap<>();

    public static DataSources getInstance() {
        return INSTANCE;
    }

    private DataSources() {

    }

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

    public DataSource getDataSource(String dataSourceName) {
        return this.dataSources.get(dataSourceName);
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

    public boolean isEmpty() {
        return this.dataSources.isEmpty();
    }
}

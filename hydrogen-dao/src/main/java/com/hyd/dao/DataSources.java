package com.hyd.dao;

import lombok.Getter;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * 管理数据源配置，允许在运行时动态添加数据源
 *
 * @author yiding.he
 */
@Getter
public class DataSources {

    @FunctionalInterface
    public interface DataSourceConsumer {

        void accept(DataSource dataSource) throws SQLException;
    }

    private static final DataSources INSTANCE = new DataSources();

    public static final String DEFAULT_DATA_SOURCE_NAME = "default";

    /**
     * dsName -> DataSource
     */
    private final Map<String, DataSource> dataSources = new ConcurrentHashMap<>();

    public static DataSources getInstance() {
        return INSTANCE;
    }

    private DataSources() {

    }

    /**
     * 释放并删除指定的数据源。仅当某些特殊的数据源实现需要手工释放时使用
     *
     * @param dataSourceName 数据源名称
     * @param finalization   如何释放数据源
     */
    public void remove(String dataSourceName, DataSourceConsumer finalization) throws DAOException {
        DataSource dataSource = dataSources.get(dataSourceName);

        if (dataSource != null) {
            dataSources.remove(dataSourceName);
            try {
                finalization.accept(dataSource);
            } catch (Exception e) {
                throw DAOException.wrap(e);
            }
        }
    }

    /**
     * 关闭所有数据源，可选，如果当服务终止时需要进行手工资源释放的话
     *
     * @param finalization 如何释放数据源
     */
    @SuppressWarnings("unused")
    public void closeAll(DataSourceConsumer finalization) {
        this.dataSources.keySet().forEach(dataSourceName -> remove(dataSourceName, finalization));
    }

    public void setDataSource(String dataSourceName, DataSource dataSource) {
        this.dataSources.put(dataSourceName, dataSource);
    }

    public DataSource getDataSource(String dataSourceName) {
        return this.dataSources.get(dataSourceName);
    }

    public boolean contains(String dsName) {
        return this.dataSources.containsKey(dsName);
    }

    /**
     * 直接获取数据库连接并执行操作，操作完成后本方法将自动释放数据库连接。
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

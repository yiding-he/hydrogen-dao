package com.hyd.dao.database.executor;

import com.hyd.dao.database.type.NameConverter;
import java.sql.Connection;

/**
 * 执行本次数据库操作的上下文信息
 */
public class ExecutionContext {

    private String dataSourceName;

    private Connection connection;

    private NameConverter nameConverter;

    public String getDataSourceName() {
        return dataSourceName;
    }

    public void setDataSourceName(String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public NameConverter getNameConverter() {
        return nameConverter;
    }

    public void setNameConverter(NameConverter nameConverter) {
        this.nameConverter = nameConverter;
    }
}

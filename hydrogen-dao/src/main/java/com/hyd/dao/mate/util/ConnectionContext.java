package com.hyd.dao.mate.util;

import com.hyd.dao.database.type.NameConverter;

import java.sql.Connection;

public class ConnectionContext {

    private final String dataSourceName;

    private final Connection connection;

    private final NameConverter nameConverter;

    public ConnectionContext(String dataSourceName, Connection connection, NameConverter nameConverter) {
        this.dataSourceName = dataSourceName;
        this.connection = connection;
        this.nameConverter = nameConverter;
    }

    public Connection getConnection() {
        return connection;
    }

    public String getDataSourceName() {
        return dataSourceName;
    }

    public NameConverter getNameConverter() {
        return nameConverter;
    }
}

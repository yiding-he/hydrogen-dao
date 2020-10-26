package com.hyd.dao.mate.util;

import com.hyd.dao.database.type.NameConverter;
import com.hyd.dao.log.Logger;

import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionContext {

    private static final Logger LOG = Logger.getLogger(ConnectionContext.class);

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

    public void commit() {
        try {
            if (!connection.getAutoCommit() && !connection.isClosed()) {
                connection.commit();
            }
            LOG.debug("Connection committed.");
        } catch (SQLException e) {
            LOG.error("Error committing database connection, dataSource=" + this.dataSourceName, e);
        }
    }

    public void rollback() {
        try {
            if (!connection.getAutoCommit() && !connection.isClosed()) {
                connection.rollback();
            }
            LOG.debug("Connection rolled back.");
        } catch (SQLException e) {
            LOG.error("Error rolling back database connection, dataSource=" + this.dataSourceName, e);
        }
    }

    public void close() {
        try {
            if (!connection.isClosed()) {
                connection.close();
            }
            LOG.debug("Connection closed.");
        } catch (SQLException e) {
            LOG.error("Error closing database connection, dataSource=" + this.dataSourceName, e);
        }
    }

    public void closeIfAutoCommit() {
        try {
            if (connection.getAutoCommit()) {
                close();
            }
        } catch (SQLException e) {
            LOG.error("Error closing database connection, dataSource=" + this.dataSourceName, e);
        }
    }
}

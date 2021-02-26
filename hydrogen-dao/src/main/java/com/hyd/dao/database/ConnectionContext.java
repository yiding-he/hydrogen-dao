package com.hyd.dao.database;

import com.hyd.dao.DAOException;
import com.hyd.dao.database.dialects.Dialect;
import com.hyd.dao.database.dialects.Dialects;
import com.hyd.dao.database.type.NameConverter;
import com.hyd.dao.log.Logger;
import lombok.Builder;
import lombok.Getter;

import java.sql.Connection;
import java.sql.SQLException;

@Builder
@Getter
public class ConnectionContext {

    private static final Logger LOG = Logger.getLogger(ConnectionContext.class);

    private final String dataSourceName;

    private final Connection connection;

    private final NameConverter nameConverter;

    private final String databaseProductName;

    private final String databaseVersion;

    private final Dialect dialect;

    public static ConnectionContext create(String dataSourceName, Connection connection, NameConverter nameConverter) {
        try {
            String databaseProductName = connection.getMetaData().getDatabaseProductName();
            String databaseProductVersion = connection.getMetaData().getDatabaseProductVersion();
            return ConnectionContext.builder()
                .databaseProductName(databaseProductName)
                .databaseVersion(databaseProductVersion)
                .dataSourceName(dataSourceName)
                .connection(connection)
                .nameConverter(nameConverter)
                .dialect(Dialects.getDialect(connection))
                .build();
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    public static ConnectionContext create(Connection connection) {
        return create("", connection, NameConverter.DEFAULT);
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

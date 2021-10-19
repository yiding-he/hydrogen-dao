package com.hyd.dao.database;

import com.hyd.dao.DAOException;
import com.hyd.dao.database.dialects.Dialect;
import com.hyd.dao.database.dialects.Dialects;
import com.hyd.dao.database.type.NameConverter;
import com.hyd.dao.log.Logger;
import lombok.Builder;
import lombok.Getter;

import javax.sql.PooledConnection;
import java.sql.Connection;
import java.sql.SQLException;

/**
 *
 */
@Builder
@Getter
public class ConnectionContext {

    private static final Logger LOG = Logger.getLogger(ConnectionContext.class);

    private final String dataSourceName;

    private final ConnectionHolder connectionHolder;

    private final NameConverter nameConverter;

    private final String databaseProductName;

    private final String databaseVersion;

    private final Dialect dialect;

    private boolean disposed;

    public static ConnectionContext create(
        String dataSourceName, ConnectionHolder connectionHolder, NameConverter nameConverter) {
        try {
            Connection connection = connectionHolder.getConnection();
            String databaseProductName = connection.getMetaData().getDatabaseProductName();
            String databaseProductVersion = connection.getMetaData().getDatabaseProductVersion();
            return ConnectionContext.builder()
                .databaseProductName(databaseProductName)
                .databaseVersion(databaseProductVersion)
                .dataSourceName(dataSourceName)
                .connectionHolder(connectionHolder)
                .nameConverter(nameConverter)
                .dialect(Dialects.getDialect(connection))
                .build();
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    public static ConnectionContext create(Connection connection) {
        return create("", ConnectionHolder.fromStatic(connection), NameConverter.DEFAULT);
    }

    private void validateDisposeStatus() {
        if (this.disposed) {
            throw new IllegalStateException("ConnectionContext is disposed.");
        }
    }

    /**
     * 从 ConnectionHolder 获得连接对象，然后尝试得到原始的连接。
     */
    public Connection getDriverConnection() {
        try {
            Connection connection = this.connectionHolder.getConnection();
            return connection instanceof PooledConnection ? ((PooledConnection) connection).getConnection() : connection;
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    /**
     * 从 ConnectionHolder 直接获得连接对象。
     */
    public Connection getConnection() {
        return this.connectionHolder.getConnection();
    }

    /**
     * 提交事务并关闭连接，仅当在事务结束时调用
     */
    public void commit() {
        try {
            validateDisposeStatus();
            Connection connection = connectionHolder.getConnection();
            if (!connection.getAutoCommit() && !connection.isClosed()) {
                connection.commit();
                connection.close();
            }
            disposed = true;
            LOG.debug("Connection committed.");
        } catch (SQLException e) {
            LOG.error("Error committing database connection, dataSource=" + this.dataSourceName, e);
        }
    }

    /**
     * 回滚事务并关闭连接，仅当在事务结束时调用
     */
    public void rollback() {
        try {
            validateDisposeStatus();
            Connection connection = connectionHolder.getConnection();
            if (!connection.getAutoCommit() && !connection.isClosed()) {
                connection.rollback();
                connection.close();
            }
            disposed = true;
            LOG.debug("Connection rolled back.");
        } catch (SQLException e) {
            LOG.error("Error rolling back database connection, dataSource=" + this.dataSourceName, e);
        }
    }

    /**
     * 关闭连接，仅当不在事务中时调用
     */
    public void closeIfAutoCommit() {
        try {
            Connection connection = connectionHolder.getConnection();
            if (connection.getAutoCommit()) {
                close();
            }
        } catch (SQLException e) {
            LOG.error("Error closing database connection, dataSource=" + this.dataSourceName, e);
        }
    }

    /**
     * 关闭连接
     */
    private void close() {
        try {
            validateDisposeStatus();
            Connection connection = connectionHolder.getConnection();
            if (!connection.isClosed()) {
                connection.close();
            }
            disposed = true;
            LOG.debug("Connection closed.");
        } catch (SQLException e) {
            LOG.error("Error closing database connection, dataSource=" + this.dataSourceName, e);
        }
    }
}

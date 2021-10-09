package com.hyd.dao.database;

import java.sql.Connection;
import java.util.function.Supplier;

/**
 * Used when
 * 1. Connection object is not reliable
 * 2. Need lazy initialization for connection
 * However, default implementation does not reassign {@link #connectionInstance}
 */
public class ConnectionHolder {

    private final Supplier<Connection> connectionSupplier;

    private Connection connectionInstance;

    public static ConnectionHolder fromStatic(Connection connection) {
        return new ConnectionHolder(() -> connection);
    }

    public static ConnectionHolder fromSupplier(Supplier<Connection> connectionSupplier) {
        return new ConnectionHolder(connectionSupplier);
    }

    private ConnectionHolder(Supplier<Connection> connectionSupplier) {
        this.connectionSupplier = connectionSupplier;
        this.connectionInstance = connectionSupplier.get();
    }

    public Connection getConnection() {
        if (connectionInstance == null) {
            connectionInstance = this.connectionSupplier.get();
        }
        return connectionInstance;
    }
}

package com.hyd.dao.database;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author yidin
 */
public enum DatabaseType {

    MySQL(".*MySQL.*"),
    Oracle(".*Oracle.*"),
    SQLServer(".*Microsoft SQL Server.*"),
    HSQLDB(".*(HSQL|H2).*"),
    Others(null)

    ///////////////////////////////////////////////

    ;

    private String pattern;

    DatabaseType(String pattern) {
        this.pattern = pattern;
    }

    public boolean matchPattern(String databaseProductionInfo) {
        return this.pattern != null && databaseProductionInfo.matches(this.pattern);
    }

    public boolean isSequenceSupported() {
        return this == Oracle;
    }

    ///////////////////////////////////////////////

    private static String getDatabaseTypeName(Connection connection) throws SQLException {
        return connection.getMetaData().getDatabaseProductName() + " "
                + connection.getMetaData().getDatabaseProductVersion();
    }

    public static DatabaseType of(Connection connection) throws SQLException {
        String typeName = getDatabaseTypeName(connection);

        for (DatabaseType type : DatabaseType.values()) {
            if (type.matchPattern(typeName)) {
                return type;
            }
        }

        return DatabaseType.Others;
    }

}

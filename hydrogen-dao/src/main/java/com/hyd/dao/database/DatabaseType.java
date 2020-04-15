package com.hyd.dao.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.regex.Pattern;

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

    private final Pattern pattern;

    DatabaseType(String pattern) {
        this.pattern = Pattern.compile(pattern);
    }

    public boolean matchPattern(String databaseProductionInfo) {
        return this.pattern != null && this.pattern.matcher(databaseProductionInfo).matches();
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

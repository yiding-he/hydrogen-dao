package com.hyd.dao.database;

import com.hyd.dao.DAOException;

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
        this.pattern = pattern != null ? Pattern.compile(pattern) : null;
    }

    public boolean matchPattern(String databaseProductionInfo) {
        return this.pattern != null && this.pattern.matcher(databaseProductionInfo).matches();
    }

    public boolean isSequenceSupported() {
        return this == Oracle;
    }

    ///////////////////////////////////////////////

    private static String getDatabaseTypeName(Connection connection) throws DAOException {
        try {
            return connection.getMetaData().getDatabaseProductName() + " "
                    + connection.getMetaData().getDatabaseProductVersion();
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    public static DatabaseType of(Connection connection) throws DAOException {
        String typeName = getDatabaseTypeName(connection);

        for (DatabaseType type : DatabaseType.values()) {
            if (type.matchPattern(typeName)) {
                return type;
            }
        }

        return DatabaseType.Others;
    }

}

package com.hyd.dao.database;

/**
 * @author yidin
 */
public enum JDBCDriver {

    MySQL("jdbc:mysql:", "com.mysql.jdbc.Driver"),
    Oracle("jdbc:oracle:", "oracle.jdbc.OracleDriver"),
    H2("jdbc:h2:", "org.h2.Driver"),
    HSQLDB("jdbc:hsqldb:", "org.hsqldb.jdbc.JDBCDriver"),
    SQLServer("jdbc:sqlserver:", "com.microsoft.sqlserver.jdbc.SQLServerDriver"),
    DB2("jdbc:db2:", "com.ibm.db2.jcc.DB2Driver"),
    SQLiteJDBC("jdbc:sqlite:", "org.sqlite.JDBC"),
    PostgreSQL("jdbc:postgresql:", "org.postgresql.Driver"),
    ODBC("jdbc:odbc:", "sun.jdbc.odbc.JdbcOdbcDriver"),

    ///////////////////////////////////////////////
    ;

    private String schemaPrefix;

    private String driverClass;

    JDBCDriver(String schemaPrefix, String driverClass) {
        this.schemaPrefix = schemaPrefix;
        this.driverClass = driverClass;
    }

    public String getSchemaPrefix() {
        return schemaPrefix;
    }

    public String getDriverClass() {
        return driverClass;
    }

    // 检查 JDBC Class 是否存在
    public boolean isAvailable() {
        try {
            Class.forName(this.driverClass);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static JDBCDriver getDriverByUrl(String jdbcUrl) {
        for (JDBCDriver driver : values()) {
            if (jdbcUrl.startsWith(driver.schemaPrefix) && driver.isAvailable()) {
                return driver;
            }
        }

        return null;
    }
}

package com.hyd.dao.database;

import com.hyd.dao.mate.util.Cls;

import java.sql.Driver;

/**
 * 根据 JDBC URL 来猜测对应的 Driver 类
 *
 * @author yiding.he@gmail.com
 */
public enum JDBCDriver {

    MySQL("jdbc:mysql:", "com.mysql.cj.jdbc.Driver", "com.mysql.jdbc.Driver"),
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

    private final String schemaPrefix;

    private final String[] driverClasses;

    private Class<? extends Driver> availableDriver;

    JDBCDriver(String schemaPrefix, String... driverClasses) {
        this.schemaPrefix = schemaPrefix;
        this.driverClasses = driverClasses;
    }

    public String getSchemaPrefix() {
        return schemaPrefix;
    }

    public String[] getDriverClasses() {
        return driverClasses;
    }

    public Class<? extends Driver> getAvailableDriver() {
        return this.availableDriver;
    }

    // 检查 JDBC Class 是否存在
    @SuppressWarnings("unchecked")
    public boolean isAvailable() {
        for (String driverClass : driverClasses) {
            Class<?> type = Cls.getType(driverClass);
            if (type != null && Driver.class.isAssignableFrom(type)) {
                availableDriver = (Class<? extends Driver>) type;
                return true;
            }
        }
        return false;
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

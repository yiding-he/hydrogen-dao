package com.hyd.dao.database;

import com.hyd.dao.DAOException;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.*;
import java.util.logging.Logger;

/**
 * 实现一个最简单的数据源
 * created at 2015/3/12
 *
 * @author Yiding
 */
public class NonPooledDataSource implements DataSource {

    private String driverClassName;

    private String url;

    private String username;

    private String password;

    private PrintWriter logWriter;

    public NonPooledDataSource() {
    }

    public NonPooledDataSource(String driverClassName, String url) {
        this.driverClassName = driverClassName;
        this.url = url;

        initDriver();
    }

    public NonPooledDataSource(String driverClassName, String url, String username, String password) {
        this.driverClassName = driverClassName;
        this.url = url;
        this.username = username;
        this.password = password;

        initDriver();
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    private void initDriver() {

        try {
            Class type = Class.forName(this.driverClassName);
            Driver driver = (Driver) type.newInstance();
            DriverManager.registerDriver(driver);
        } catch (Exception e) {
            throw new DAOException("Error registering JDBC driver", e);
        }
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(this.url, this.username, this.password);
    }

    public Connection getConnection(String username, String password) throws SQLException {
        return DriverManager.getConnection(this.url, username, password);
    }

    public PrintWriter getLogWriter() throws SQLException {
        return this.logWriter;
    }

    public void setLogWriter(PrintWriter out) throws SQLException {
        this.logWriter = out;
    }

    public int getLoginTimeout() throws SQLException {
        throw new UnsupportedOperationException("Not supported by NonPooledDataSource");
    }

    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }

    public void setLoginTimeout(int seconds) throws SQLException {
        throw new UnsupportedOperationException("Not supported by NonPooledDataSource");
    }

    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new SQLException("NonPooledDataSource is not a wrapper.");
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }
}

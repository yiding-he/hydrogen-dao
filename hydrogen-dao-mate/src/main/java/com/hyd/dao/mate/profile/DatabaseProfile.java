package com.hyd.dao.mate.profile;

import java.util.Objects;

public class DatabaseProfile {

    private String driverClassName;

    private String jdbcUrl;

    private String username;

    private String password;

    public String getDriverClassName() {
        return driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public void setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DatabaseProfile that = (DatabaseProfile) o;
        return driverClassName.equals(that.driverClassName) &&
            jdbcUrl.equals(that.jdbcUrl) &&
            username.equals(that.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(driverClassName, jdbcUrl, username);
    }

    @Override
    public String toString() {
        return this.jdbcUrl + " [" + this.username + "]";
    }
}

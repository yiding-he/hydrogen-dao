package com.hyd.dao.mate;

import java.sql.Connection;

public class MainFrame extends MainFrameLayout {

    private Connection connection;

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public Connection getConnection() {
        return connection;
    }

    @Override
    public void initialize() {

    }
}

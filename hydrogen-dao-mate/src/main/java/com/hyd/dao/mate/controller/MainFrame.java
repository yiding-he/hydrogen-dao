package com.hyd.dao.mate.controller;

import com.hyd.dao.mate.ui.MainFrameLayout;

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
        this.projectConfigPanel.readConfig();
    }
}

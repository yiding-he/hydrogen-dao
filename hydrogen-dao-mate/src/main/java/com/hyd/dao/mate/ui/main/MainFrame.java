package com.hyd.dao.mate.ui.main;

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
        // 之前有过读取配置的步骤，现已删掉
    }
}

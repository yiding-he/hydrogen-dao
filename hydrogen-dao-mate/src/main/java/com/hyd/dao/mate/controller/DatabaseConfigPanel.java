package com.hyd.dao.mate.controller;

import com.hyd.dao.database.NonPooledDataSource;
import com.hyd.dao.mate.CodeMateMain;
import com.hyd.dao.mate.swing.Swing;
import com.hyd.dao.mate.ui.DatabaseConfigLayout;
import com.hyd.dao.mate.util.Events;
import com.hyd.dao.mate.util.Listeners;
import java.io.*;
import java.nio.file.*;
import java.sql.SQLException;
import java.util.Properties;
import javax.swing.BorderFactory;

public class DatabaseConfigPanel extends DatabaseConfigLayout {

    public DatabaseConfigPanel() {
        setBorder(BorderFactory.createTitledBorder("数据库配置"));
        this.readFromAppButton.addActionListener(event -> readDatabaseConfig());
        this.openDatabaseButton.addActionListener(event -> openDatabase());
    }

    private void readDatabaseConfig() {
        String configFilePath =
            CodeMateMain.getMainFrame().getProjectConfigPanel().getConfigFilePath().getValue();

        if (configFilePath == null || configFilePath.isEmpty()) {
            Swing.alertError("错误", "请配置 application.properties 路径");
            return;
        }

        Path path = Paths.get(configFilePath);
        if (!Files.exists(path)) {
            Swing.alertError("错误", "找不到文件 '" + configFilePath + "'");
            return;
        }

        try (FileInputStream fis = new FileInputStream(new File(configFilePath))) {
            Properties properties = new Properties();
            properties.load(fis);
            this.jdbcUrl.setValue(properties.getProperty("spring.datasource.url"));
            this.driverClassName.setValue(properties.getProperty("spring.datasource.driver-class-name"));
            this.databaseUser.setValue(properties.getProperty("spring.datasource.username"));
            this.databasePass.setValue(properties.getProperty("spring.datasource.password"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openDatabase() {
        NonPooledDataSource ds = new NonPooledDataSource(
            driverClassName.getValue(), jdbcUrl.getValue(), databaseUser.getValue(), databasePass.getValue()
        );

        try {
            CodeMateMain.getMainFrame().setConnection(ds.getConnection());
            Swing.alertInfo("数据库已连接", "数据库已成功连接。");
            openDatabaseButton.setEnabled(false);

            Listeners.publish(Events.DatabaseConnected);

            CodeMateMain.getMainFrame().openTab(1);
            CodeMateMain.getMainFrame().getCreatePojoPanel().reset();

        } catch (SQLException e) {
            Swing.alertError("失败", "无法连接到数据库：" + e);
        }
    }
}

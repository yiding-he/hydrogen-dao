package com.hyd.dao.mate.controller;

import com.hyd.dao.mate.CodeMateMain;
import com.hyd.dao.mate.swing.Swing;
import com.hyd.dao.mate.ui.DatabaseConfigLayout;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class DatabaseConfigPanel extends DatabaseConfigLayout {

    public DatabaseConfigPanel() {
        setBorder(BorderFactory.createTitledBorder("数据库配置"));
        this.readFromAppButton.addActionListener(event -> readDatabaseConfig());
        this.openDatabaseButton.addActionListener(event -> openDatabase());
    }

    private void openDatabase() {

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
}

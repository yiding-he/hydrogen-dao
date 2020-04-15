package com.hyd.dao.mate.ui.main.db;

import com.hyd.dao.database.NonPooledDataSource;
import com.hyd.dao.mate.CodeMateMain;
import com.hyd.dao.mate.swing.Swing;
import com.hyd.dao.mate.ui.main.MainFrame;
import com.hyd.dao.mate.util.Events;
import com.hyd.dao.mate.util.Listeners;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Properties;
import javax.swing.BorderFactory;

public class DatabaseConfigPanel extends DatabaseConfigLayout {

    private String recentPath = ".";

    public DatabaseConfigPanel() {
        setBorder(BorderFactory.createTitledBorder("数据库配置"));
        this.readFromAppButton.addActionListener(event -> readDatabaseConfig());
        this.openDatabaseButton.addActionListener(event -> openDatabase());
    }

    private void readDatabaseConfig() {
        String configFilePath = Swing.chooseFile(new File(recentPath));
        if (configFilePath == null) {
            return;
        }

        this.recentPath = configFilePath;
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
            MainFrame mainFrame = CodeMateMain.getMainFrame();
            if (mainFrame.getConnection() != null) {
                mainFrame.getConnection().close();
            }

            mainFrame.setConnection(ds.getConnection());
            Swing.alertInfo("数据库已连接", "数据库已成功连接。");
            openDatabaseButton.setEnabled(false);

            Listeners.publish(Events.DatabaseConnected);

            mainFrame.openTab(1);
            mainFrame.getCreatePojoPanel().reset();

        } catch (SQLException e) {
            Swing.alertError("失败", "无法连接到数据库：" + e);
        }
    }
}

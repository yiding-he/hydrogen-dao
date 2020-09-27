package com.hyd.dao.mate.ui.main.db;

import com.hyd.dao.mate.swing.FormPanel;
import com.hyd.dao.mate.swing.form.ComboBoxField;
import com.hyd.dao.mate.swing.form.TextField;
import javax.swing.JButton;

public class DatabaseConfigLayout extends FormPanel {

    protected final ComboBoxField recentProfiles = new ComboBoxField("最近使用的配置");

    protected final TextField jdbcUrl = new TextField("JDBC 数据库地址");

    protected final TextField driverClassName = new TextField("驱动类名");

    protected final TextField databaseUser = new TextField("数据库用户名");

    protected final TextField databasePass = new TextField("数据库密码");

    protected final JButton readFromAppButton = new JButton("打开 application.properties 文件...");

    protected final JButton openDatabaseButton = new JButton("连接数据库");

    public DatabaseConfigLayout() {
        addFormField(recentProfiles);
        addFormField(jdbcUrl);
        addFormField(driverClassName);
        addFormField(databaseUser);
        addFormField(databasePass);

        addButton(readFromAppButton);
        addButton(openDatabaseButton);

        openDatabaseButton.setEnabled(false);

        jdbcUrl.setOnValueChanged(text -> textChanged());
        driverClassName.setOnValueChanged(text -> textChanged());
        databaseUser.setOnValueChanged(text -> textChanged());
        databasePass.setOnValueChanged(text -> textChanged());
    }

    private void textChanged() {
        if (!jdbcUrl.getValue().isEmpty() &&
            !driverClassName.getValue().isEmpty() &&
            !databaseUser.getValue().isEmpty()) {

            openDatabaseButton.setEnabled(true);
        } else {
            openDatabaseButton.setEnabled(false);
        }
    }
}

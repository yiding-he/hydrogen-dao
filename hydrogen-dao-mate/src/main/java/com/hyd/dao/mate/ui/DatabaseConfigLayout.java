package com.hyd.dao.mate.ui;

import com.hyd.dao.mate.swing.FormPanel;
import com.hyd.dao.mate.swing.TextField;

import javax.swing.*;

public class DatabaseConfigLayout extends FormPanel {

    protected final TextField jdbcUrl = new TextField("JDBC 数据库地址");

    protected final TextField driverClassName = new TextField("驱动类名");

    protected final TextField databaseUser = new TextField("数据库用户名");

    protected final TextField databasePass = new TextField("数据库密码");

    protected final JButton readFromAppButton = new JButton("从 application.properties 读取");

    protected final JButton openDatabaseButton = new JButton("连接数据库");

    public DatabaseConfigLayout() {
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

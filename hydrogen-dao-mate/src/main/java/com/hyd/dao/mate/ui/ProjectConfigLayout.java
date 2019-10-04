package com.hyd.dao.mate.ui;

import com.hyd.dao.mate.swing.DirectoryField;
import com.hyd.dao.mate.swing.FormPanel;
import com.hyd.dao.mate.swing.TextField;

import javax.swing.*;

public class ProjectConfigLayout extends FormPanel {

    protected final DirectoryField srcPath;

    protected final TextField applicationPropertiesPath;

    protected final TextField pojoPackage;

    protected final JButton saveButton;

    public ProjectConfigLayout() {
        setBorder(BorderFactory.createTitledBorder("路径配置"));

        addFormField(srcPath = new DirectoryField("源码目录"));
        addFormField(applicationPropertiesPath = new TextField("application.properties 路径"));
        addFormField(pojoPackage = new TextField("pojo 包名"));

        addButton(saveButton = new JButton("保存"));
    }

    public DirectoryField getSrcPath() {
        return srcPath;
    }

    public TextField getPojoPackage() {
        return pojoPackage;
    }

}

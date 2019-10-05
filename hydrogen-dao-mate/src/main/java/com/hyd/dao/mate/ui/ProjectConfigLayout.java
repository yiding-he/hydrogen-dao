package com.hyd.dao.mate.ui;

import com.hyd.dao.mate.swing.DirectoryField;
import com.hyd.dao.mate.swing.FileField;
import com.hyd.dao.mate.swing.FormPanel;
import com.hyd.dao.mate.swing.TextField;

import javax.swing.*;

public class ProjectConfigLayout extends FormPanel {

    protected final DirectoryField srcPath = new DirectoryField("源码目录");

    protected final FileField configFilePath = new FileField("application.properties 路径");

    protected final TextField pojoPackage = new TextField("pojo 包名");

    protected final JButton saveButton = new JButton("保存");

    public ProjectConfigLayout() {
        setBorder(BorderFactory.createTitledBorder("路径配置"));

        addFormField(srcPath);
        addFormField(configFilePath);
        addFormField(pojoPackage);

        addButton(saveButton);
    }

    public DirectoryField getSrcPath() {
        return srcPath;
    }

    public TextField getPojoPackage() {
        return pojoPackage;
    }

    public FileField getConfigFilePath() {
        return configFilePath;
    }
}

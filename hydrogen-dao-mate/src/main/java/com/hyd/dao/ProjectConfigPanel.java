package com.hyd.dao;

public class ProjectConfigPanel extends FormPanel {

    public ProjectConfigPanel() {
        addFormField(new DirectoryField("源码目录"));
        addFormField(new TextField("pojo 包名"));
    }
}

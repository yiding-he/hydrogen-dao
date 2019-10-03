package com.hyd.dao.mate.controller;

import com.hyd.dao.mate.ui.MainFrameLayout;

public class MainFrame extends MainFrameLayout {

    public ProjectConfigPanel getProjectConfigPanel() {
        return this.projectConfigPanel;
    }

    @Override
    public void initialize() {
        this.projectConfigPanel.readConfig();
    }
}

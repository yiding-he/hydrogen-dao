package com.hyd.dao.mate.controller;

import com.hyd.dao.mate.MateConfiguration;
import com.hyd.dao.mate.swing.Swing;
import com.hyd.dao.mate.ui.ProjectConfigLayout;
import com.hyd.dao.mate.util.Configuration;
import com.hyd.dao.mate.util.Events;
import com.hyd.dao.mate.util.Listeners;

import static com.hyd.dao.mate.util.Configuration.CONFIG_FILE_PATH;

public class ProjectConfigPanel extends ProjectConfigLayout {

    public ProjectConfigPanel() {
        this.saveButton.addActionListener(event -> saveConfig());
    }

    public void saveConfig() {
        MateConfiguration c = new MateConfiguration();
        c.setSrcPath(srcPath.getValue());
        c.setPojoPackage(pojoPackage.getValue());
        c.setConfigFilePath(configFilePath.getValue());

        Configuration.saveConfiguration(c, CONFIG_FILE_PATH);
        Swing.alertInfo("保存配置", "保存完毕。");

        Listeners.publish(Events.ConfigUpdated);
    }

    public void readConfig() {
        MateConfiguration mateConfiguration =
            Configuration.readConfiguration(CONFIG_FILE_PATH, MateConfiguration.class);

        if (mateConfiguration != null) {
            this.srcPath.setValue(mateConfiguration.getSrcPath());
            this.pojoPackage.setValue(mateConfiguration.getPojoPackage());
            this.configFilePath.setValue(mateConfiguration.getConfigFilePath());
        }
    }
}

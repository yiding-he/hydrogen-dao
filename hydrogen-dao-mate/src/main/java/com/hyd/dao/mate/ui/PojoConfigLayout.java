package com.hyd.dao.mate.ui;

import com.hyd.dao.mate.swing.*;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

public class PojoConfigLayout extends JPanel {

    protected final TextField pojoName = new TextField("POJO 类名");

    public PojoConfigLayout() {
        setBorder(BorderFactory.createEmptyBorder(0, Swing.PADDING, 0, 0));

        FormPanel formPanel = new FormPanel();
        formPanel.setBorder(BorderFactory.createTitledBorder("POJO 配置"));
        formPanel.addFormField(pojoName);

        add(formPanel);
        Swing.fillWith(this, formPanel, Swing.TOP, Swing.LEFT, Swing.RIGHT);
    }
}

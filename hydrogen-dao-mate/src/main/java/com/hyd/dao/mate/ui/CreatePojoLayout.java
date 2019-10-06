package com.hyd.dao.mate.ui;

import com.hyd.dao.mate.controller.PojoCodePanel;
import com.hyd.dao.mate.controller.TableListPanel;
import com.hyd.dao.mate.swing.Swing;

import javax.swing.*;

public class CreatePojoLayout extends JPanel {

    protected final PojoCodePanel pojoCodePanel = new PojoCodePanel();

    protected final TableListPanel tableListPanel = new TableListPanel();

    public CreatePojoLayout() {
        JSplitPane jSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tableListPanel, pojoCodePanel);
        jSplitPane.setBorder(null);
        jSplitPane.setResizeWeight(0.5);
        Swing.fillWith(this, jSplitPane);
    }
}

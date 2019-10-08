package com.hyd.dao.mate.ui;

import com.hyd.dao.mate.controller.PojoConfigPanel;
import com.hyd.dao.mate.controller.TableListPanel;
import com.hyd.dao.mate.swing.Swing;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

public class CreatePojoLayout extends JPanel {

    protected final PojoConfigPanel pojoConfigPanel = new PojoConfigPanel();

    protected final TableListPanel tableListPanel = new TableListPanel();

    public CreatePojoLayout() {
        JSplitPane jSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tableListPanel, pojoConfigPanel);
        jSplitPane.setBorder(null);
        jSplitPane.setResizeWeight(0.5);
        Swing.fillWith(this, jSplitPane);
    }
}

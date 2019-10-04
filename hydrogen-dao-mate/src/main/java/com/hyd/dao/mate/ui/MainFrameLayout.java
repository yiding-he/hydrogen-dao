package com.hyd.dao.mate.ui;

import com.hyd.dao.mate.controller.CreatePojoPanel;
import com.hyd.dao.mate.controller.DatabaseConfigPanel;
import com.hyd.dao.mate.controller.ProjectConfigPanel;
import com.hyd.dao.mate.swing.InitializableJFrame;
import com.hyd.dao.mate.swing.Swing;

import javax.swing.*;
import java.awt.*;

import static com.hyd.dao.mate.swing.Swing.PADDING;
import static com.hyd.dao.mate.swing.Swing.vBox;
import static javax.swing.SpringLayout.*;

public class MainFrameLayout extends InitializableJFrame {

    protected final ProjectConfigPanel projectConfigPanel = new ProjectConfigPanel();

    protected final DatabaseConfigPanel databaseConfigPanel = new DatabaseConfigPanel();

    protected final CreatePojoPanel createPojoPanel = new CreatePojoPanel();

    protected JTabbedPane tpMain = new JTabbedPane(SwingConstants.TOP);

    public MainFrameLayout() throws HeadlessException {
        setTitle("代码生成工具");
        SpringLayout layout = new SpringLayout();

        Container contentPane = getContentPane();
        contentPane.setLayout(layout);
        contentPane.add(tpMain);

        layoutComponents(layout);
        createTabs();
    }

    private void layoutComponents(SpringLayout layout) {
        Container parent = getContentPane();

        layout.putConstraint(NORTH, tpMain, PADDING, NORTH, parent);
        layout.putConstraint(SOUTH, tpMain, -PADDING, SOUTH, parent);
        layout.putConstraint(WEST, tpMain, PADDING, WEST, parent);
        layout.putConstraint(EAST, tpMain, -PADDING, EAST, parent);
    }

    private void createTabs() {
        JPanel vBox = vBox(PADDING, projectConfigPanel, databaseConfigPanel);
        Swing.addTab(tpMain, "项目配置", PADDING, vBox);
        Swing.addTab(tpMain, "生成 Pojo 类", PADDING, createPojoPanel);

        // Swing.highlight(vBox);
    }
}

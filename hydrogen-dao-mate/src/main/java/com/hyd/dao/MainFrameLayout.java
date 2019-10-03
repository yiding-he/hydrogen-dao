package com.hyd.dao;

import javax.swing.*;
import java.awt.*;

import static com.hyd.dao.Swing.PADDING;
import static javax.swing.SpringLayout.*;

public class MainFrameLayout extends JFrame {

    protected JTabbedPane tpMain = new JTabbedPane(SwingConstants.TOP);

    public MainFrameLayout() throws HeadlessException {
        setTitle("代码生成工具");

        SpringLayout layout = new SpringLayout();

        Container contentPane = getContentPane();
        contentPane.setLayout(layout);
        contentPane.add(tpMain);

        layoutComponents(layout);
    }

    private void layoutComponents(SpringLayout layout) {
        Container parent = getContentPane();

        layout.putConstraint(NORTH, tpMain, PADDING, NORTH, parent);
        layout.putConstraint(SOUTH, tpMain, -PADDING, SOUTH, parent);
        layout.putConstraint(WEST, tpMain, PADDING, WEST, parent);
        layout.putConstraint(EAST, tpMain, -PADDING, EAST, parent);

        tpMain.addTab("项目配置", new ProjectConfigPanel());
        tpMain.addTab("生成 Pojo 类", new JPanel());
    }
}

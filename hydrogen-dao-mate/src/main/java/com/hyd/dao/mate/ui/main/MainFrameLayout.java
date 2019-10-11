package com.hyd.dao.mate.ui.main;

import static com.hyd.dao.mate.swing.Swing.PADDING;
import static com.hyd.dao.mate.swing.Swing.vBox;
import static javax.swing.SpringLayout.*;

import com.hyd.dao.mate.swing.InitializableJFrame;
import com.hyd.dao.mate.swing.Swing;
import com.hyd.dao.mate.ui.main.db.DatabaseConfigPanel;
import com.hyd.dao.mate.ui.main.pojo.CreatePojoPanel;
import java.awt.Container;
import java.awt.HeadlessException;
import javax.swing.*;

public class MainFrameLayout extends InitializableJFrame {

    protected final DatabaseConfigPanel databaseConfigPanel = new DatabaseConfigPanel();

    protected final CreatePojoPanel createPojoPanel = new CreatePojoPanel();

    protected JTabbedPane tpMain = new JTabbedPane(SwingConstants.TOP);

    public MainFrameLayout() throws HeadlessException {
        setTitle("POJO 代码生成工具");
        SpringLayout layout = new SpringLayout();

        Container contentPane = getContentPane();
        contentPane.setLayout(layout);
        contentPane.add(tpMain);

        layoutComponents(layout);
        createTabs();
    }

    public void openTab(int index) {
        index = Math.max(0, Math.min(tpMain.getTabCount() - 1, index));
        tpMain.setSelectedIndex(index);
    }

    public CreatePojoPanel getCreatePojoPanel() {
        return createPojoPanel;
    }

    private void layoutComponents(SpringLayout layout) {
        Container parent = getContentPane();

        layout.putConstraint(NORTH, tpMain, PADDING, NORTH, parent);
        layout.putConstraint(SOUTH, tpMain, -PADDING, SOUTH, parent);
        layout.putConstraint(WEST, tpMain, PADDING, WEST, parent);
        layout.putConstraint(EAST, tpMain, -PADDING, EAST, parent);
    }

    private void createTabs() {
        Swing.addTab(tpMain, "项目配置", PADDING, vBox(PADDING, databaseConfigPanel));
        Swing.addTab(tpMain, "生成 Pojo 类", PADDING, createPojoPanel);
    }
}

package com.hyd.dao.mate.swing;

import com.hyd.dao.mate.CodeMateMain;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class Swing {

    public static final int PADDING = 10;

    public static final int SMALL_PADDING = 5;

    public static final String TOP = SpringLayout.NORTH;

    public static final String LEFT = SpringLayout.WEST;

    public static final String BOTTOM = SpringLayout.SOUTH;

    public static final String RIGHT = SpringLayout.EAST;

    private static Rectangle desktopBounds;

    private static void forEachDirection(Consumer<String> consumer) {
        Stream.of(TOP, RIGHT, BOTTOM, LEFT).forEach(consumer);

    }

    public static void init() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println(e.toString());
        }

        desktopBounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
    }

    public static void openWindow(InitializableJFrame frame, int width, int height) {

        frame.initialize();

        int x = desktopBounds.x + (desktopBounds.width - width) / 2;
        int y = desktopBounds.y + (desktopBounds.height - height) / 2;
        frame.setSize(width, height);
        frame.setLocation(x, y);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public static void alertInfo(String title, String message) {
        JOptionPane.showMessageDialog(CodeMateMain.getMainFrame(), message, title, JOptionPane.PLAIN_MESSAGE);
    }

    public static void alertError(String title, String message) {
        JOptionPane.showMessageDialog(CodeMateMain.getMainFrame(), message, title, JOptionPane.ERROR_MESSAGE);
    }

    public static String chooseDirectory(File startDir) {
        String result = null;

        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(startDir);
        chooser.setDialogTitle("选择目录");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);

        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            result = chooser.getSelectedFile().getAbsolutePath();
        }
        return result;
    }

    public static String chooseFile(File startDir) {
        String result = null;

        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(startDir);
        chooser.setDialogTitle("选择文件");
        chooser.setAcceptAllFileFilterUsed(false);

        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            result = chooser.getSelectedFile().getAbsolutePath();
        }
        return result;
    }

    public static void highlight(Component component) {
        component.setBackground(Color.RED);
    }

    public static void addTab(JTabbedPane tabbedPane, String tabTitle, int padding, Component content) {
        JPanel tabContainer = new JPanel();
        tabContainer.setLayout(new BorderLayout());
        tabContainer.setBorder(BorderFactory.createEmptyBorder(padding, padding, padding, padding));
        tabContainer.add(content, BorderLayout.CENTER);

        tabbedPane.addTab(tabTitle, tabContainer);
    }

    public static JPanel vBox(int gap, Component... components) {
        SpringLayout layout = new SpringLayout();

        JPanel panel = new JPanel();
        panel.setLayout(layout);

        for (int i = 0; i < components.length; i++) {
            Component component = components[i];
            panel.add(component);

            layout.putConstraint(SpringLayout.EAST, component, 0, SpringLayout.EAST, panel);
            layout.putConstraint(SpringLayout.WEST, component, 0, SpringLayout.WEST, panel);

            if (i == 0) {
                layout.putConstraint(SpringLayout.NORTH, component, 0, SpringLayout.NORTH, panel);
            } else {
                layout.putConstraint(SpringLayout.NORTH, component, gap, SpringLayout.SOUTH, components[i - 1]);
            }
        }

        return panel;
    }

    public static void fillWith(Container container, Component content) {
        SpringLayout layout = new SpringLayout();
        container.setLayout(layout);
        container.add(content);

        forEachDirection(d -> layout.putConstraint(d, content, 0, d, container));
    }
}

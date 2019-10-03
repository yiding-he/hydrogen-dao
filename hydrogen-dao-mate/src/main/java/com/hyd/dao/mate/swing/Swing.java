package com.hyd.dao.mate.swing;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class Swing {

    public static final int PADDING = 10;

    public static final int SMALL_PADDING = 5;

    private static Rectangle desktopBounds;

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

    public static void alertInfo(Component c, String title, String message) {
        JOptionPane.showMessageDialog(c, message, title, JOptionPane.PLAIN_MESSAGE);
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
}

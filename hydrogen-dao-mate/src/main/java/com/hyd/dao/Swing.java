package com.hyd.dao;

import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import javax.swing.*;

public class Swing {

    public static final int PADDING = 10;

    public static final int SMALL_PADDING = 5;

    private static Rectangle desktopBounds;

    static {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println(e.toString());
        }

        desktopBounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
    }

    public static void openWindow(JFrame windowInstance, int width, int height) {
        int x = desktopBounds.x + (desktopBounds.width - width) / 2;
        int y = desktopBounds.y + (desktopBounds.height - height) / 2;
        windowInstance.setSize(width, height);
        windowInstance.setLocation(x, y);
        windowInstance.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        windowInstance.setVisible(true);
    }
}

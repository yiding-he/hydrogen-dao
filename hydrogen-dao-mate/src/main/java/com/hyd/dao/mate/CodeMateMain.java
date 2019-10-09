package com.hyd.dao.mate;

import com.hyd.dao.mate.swing.Swing;

public class CodeMateMain {

    private static MainFrame mainFrame;

    public static MainFrame getMainFrame() {
        return mainFrame;
    }

    public static void main(String[] args) throws Exception {
        Swing.init();

        mainFrame = new MainFrame();
        Swing.openWindow(mainFrame, 700, 500);
    }
}

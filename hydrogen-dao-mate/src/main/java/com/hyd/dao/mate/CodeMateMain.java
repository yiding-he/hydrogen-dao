package com.hyd.dao.mate;

import com.hyd.dao.mate.swing.Swing;
import com.hyd.dao.mate.ui.main.MainFrame;
import com.hyd.i18n.I18n;

public class CodeMateMain {

    public static final I18n I18N = I18n.getInstance("i18n.ui");

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

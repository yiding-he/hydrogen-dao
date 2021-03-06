package com.hyd.dao.mate.ui.result;

import static com.hyd.dao.mate.swing.Swing.PADDING;
import static javax.swing.BorderFactory.createEmptyBorder;

import com.hyd.dao.mate.swing.InitializableJFrame;
import java.awt.BorderLayout;
import java.awt.Component;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class PojoResultLayout extends InitializableJFrame {

    protected final JTextArea textArea = new JTextArea();

    protected final JButton copyButton = new JButton("复制到剪贴板");

    PojoResultLayout() {
        JPanel jPanel = new JPanel();
        jPanel.setBorder(createEmptyBorder(PADDING, PADDING, PADDING, PADDING));
        jPanel.setLayout(new BorderLayout());
        jPanel.add(new JScrollPane(textArea), BorderLayout.CENTER);
        jPanel.add(buttons(), BorderLayout.NORTH);
        setContentPane(jPanel);
    }

    private Component buttons() {
        Box buttons = new Box(BoxLayout.X_AXIS);
        buttons.setBorder(createEmptyBorder(0, 0, PADDING, 0));
        buttons.add(copyButton);
        return buttons;
    }
}

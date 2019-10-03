package com.hyd.dao;

import static javax.swing.SpringLayout.*;

import java.awt.Container;
import java.awt.HeadlessException;
import javax.swing.*;

public class SelectApplicationClassDialog extends JFrame {

    private JLabel title = new JLabel("选择项目的主类");

    private JList<String> mainClassesList = new JList<>();

    private JButton okButton = new JButton("确定");

    public SelectApplicationClassDialog() throws HeadlessException {
        setTitle("选择项目的主类");

        Container contentPane = getContentPane();
        contentPane.add(title);
        contentPane.add(mainClassesList);
        contentPane.add(okButton);

        mainClassesList.setSize(100, 100);

        SpringLayout layout = new SpringLayout();
        setLayout(layout);

        layout.putConstraint(WEST, title, 5, WEST, contentPane);
        layout.putConstraint(NORTH, title, 5, NORTH, contentPane);

        layout.putConstraint(NORTH, mainClassesList, 5, SOUTH, title);

    }
}

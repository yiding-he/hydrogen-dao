package com.hyd.dao;

import javax.swing.*;

import static javax.swing.BoxLayout.X_AXIS;

public class DirectoryField extends FormField<String> {

    private JTextField textField = textField();

    private JButton chooseButton = new JButton("...");

    public DirectoryField(String labelText) {
        super(labelText);

        Box hbox = new Box(X_AXIS);
        hbox.add(textField);
        hbox.add(createHorizontalStrut(Swing.SMALL_PADDING));
        hbox.add(chooseButton);

        this.add(hbox);
    }

    @Override
    public String getValue() {
        return textField.getText();
    }
}

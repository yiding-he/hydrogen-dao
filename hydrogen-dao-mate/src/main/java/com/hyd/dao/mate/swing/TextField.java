package com.hyd.dao.mate.swing;

import javax.swing.*;

public class TextField extends FormField<String> {

    private JTextField textField = textField();

    public TextField(String label) {
        super(label);
        add(textField);
    }

    @Override
    public String getValue() {
        return this.textField.getText();
    }

    @Override
    public void setValue(String value) {
        this.textField.setText(value);
    }
}

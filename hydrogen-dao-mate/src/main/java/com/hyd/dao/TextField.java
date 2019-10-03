package com.hyd.dao;

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
}

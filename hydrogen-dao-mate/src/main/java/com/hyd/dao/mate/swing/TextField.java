package com.hyd.dao.mate.swing;

import javax.swing.*;

public class TextField extends FormField<String> {

    private JTextField textField = textField();

    public TextField(String label) {
        super(label);
        add(textField);

        Swing.addChangeListener(this.textField, text -> {
            if (this.onValueChanged != null) {
                this.onValueChanged.accept(text);
            }
        });
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

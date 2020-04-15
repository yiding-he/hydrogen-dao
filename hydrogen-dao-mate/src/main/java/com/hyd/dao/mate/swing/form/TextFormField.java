package com.hyd.dao.mate.swing.form;

import javax.swing.JTextField;

public abstract class TextFormField extends FormField<String> {

    protected final JTextField textField = textField();

    public TextFormField(String labelText) {
        super(labelText);
    }

    @Override
    public String getValue() {
        return textField.getText();
    }

    @Override
    public void setValue(String value) {
        this.textField.setText(value);
    }
}

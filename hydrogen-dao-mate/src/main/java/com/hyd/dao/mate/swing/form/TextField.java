package com.hyd.dao.mate.swing.form;

import com.hyd.dao.mate.swing.Swing;
import java.awt.Component;
import java.util.Collections;
import java.util.List;
import javax.swing.JTextField;

public class TextField extends FormField<String> {

    private final JTextField textField = textField();

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

    @Override
    protected List<Component> getFunctionComponents() {
        return Collections.singletonList(this.textField);
    }
}

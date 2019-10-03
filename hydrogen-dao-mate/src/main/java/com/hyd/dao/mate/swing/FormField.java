package com.hyd.dao.mate.swing;

import javax.swing.*;

import static javax.swing.BoxLayout.X_AXIS;
import static javax.swing.BoxLayout.Y_AXIS;

public abstract class FormField<V> extends Box {

    protected JLabel label = new JLabel();

    protected JTextField textField() {
        return new JTextField();
    }

    public FormField(String labelText) {
        super(Y_AXIS);
        label.setText(labelText);

        Box labelBox = new Box(X_AXIS);
        labelBox.add(label);
        labelBox.add(createHorizontalGlue());

        add(labelBox);
        add(createVerticalStrut(Swing.SMALL_PADDING));
    }

    public abstract V getValue();

    public abstract void setValue(V value);
}

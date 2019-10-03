package com.hyd.dao;

import javax.swing.*;

import static com.hyd.dao.Swing.PADDING;
import static javax.swing.SpringLayout.*;

public abstract class FormPanel extends JPanel {

    private final SpringLayout layout = new SpringLayout();

    private FormField<?> lastField = null;

    public FormPanel() {
        setLayout(layout);
    }

    public void addFormField(FormField<?> formField) {
        add(formField);

        layout.putConstraint(WEST, formField, PADDING, WEST, this);
        layout.putConstraint(EAST, formField, -PADDING, EAST, this);

        if (lastField == null) {
            layout.putConstraint(NORTH, formField, PADDING, NORTH, this);
        } else {
            layout.putConstraint(NORTH, formField, PADDING, SOUTH, lastField);
        }

        lastField = formField;
    }
}

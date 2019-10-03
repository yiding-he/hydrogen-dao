package com.hyd.dao.mate.swing;

import javax.swing.*;

import static com.hyd.dao.mate.swing.Swing.PADDING;
import static javax.swing.SpringLayout.*;

public abstract class FormPanel extends JPanel {

    private final SpringLayout layout = new SpringLayout();

    private FormField<?> lastField = null;

    private Box buttons = new Box(BoxLayout.X_AXIS);

    public FormPanel() {
        setLayout(layout);
        layoutButtons();
    }

    private void layoutButtons() {
        add(buttons);

        buttons.add(Box.createHorizontalGlue());
        layout.putConstraint(WEST, buttons, PADDING, WEST, this);
        layout.putConstraint(EAST, buttons, -PADDING, EAST, this);
        layout.putConstraint(SOUTH, buttons, -PADDING, SOUTH, this);
    }

    public void addFormField(FormField<?> formField) {
        add(formField);

        layout.putConstraint(WEST, formField, PADDING, WEST, this);
        layout.putConstraint(EAST, formField, -PADDING, EAST, this);
        // layout.putConstraint(SOUTH, formField, -PADDING, NORTH, buttons);

        if (lastField == null) {
            layout.putConstraint(NORTH, formField, PADDING, NORTH, this);
        } else {
            layout.putConstraint(NORTH, formField, PADDING, SOUTH, lastField);
        }

        lastField = formField;
    }

    public void addButton(JButton button) {
        buttons.add(button);
    }
}

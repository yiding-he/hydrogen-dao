package com.hyd.dao.mate.swing;

import static com.hyd.dao.mate.swing.Swing.PADDING;
import static javax.swing.SpringLayout.*;

import com.hyd.dao.mate.swing.form.FormField;
import java.util.stream.Stream;
import javax.swing.*;

public class FormPanel extends JPanel {

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
    }

    public void addButton(JButton button) {
        buttons.add(Box.createHorizontalStrut(PADDING));
        buttons.add(button);
        layout.putConstraint(SOUTH, this, PADDING, SOUTH, buttons);
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

        layout.putConstraint(NORTH, buttons, PADDING * 2, SOUTH, formField);

        if (Stream.of(this.buttons.getComponents()).noneMatch(c -> c instanceof JButton)) {
            layout.putConstraint(SOUTH, this, PADDING, SOUTH, formField);
        }

        lastField = formField;
    }

    public void setAutoStretch(FormField<?> formField) {
        Stream.of(this.getComponents())
            .filter(c -> c instanceof FormField)
            .map(c -> (FormField) c)
            .forEach(field -> {
                if (field == formField) {
                    field.setAutoStretch(true);
                } else {
                    field.setAutoStretch(false);
                }
            });
    }
}

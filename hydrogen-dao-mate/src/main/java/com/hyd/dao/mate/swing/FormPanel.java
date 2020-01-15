package com.hyd.dao.mate.swing;

import static com.hyd.dao.mate.swing.Swing.PADDING;

import com.hyd.dao.mate.swing.form.FormField;
import com.hyd.dao.mate.swing.layout.SpringLayoutHelper;
import com.hyd.dao.mate.swing.layout.SpringLayoutHelper.Edge;
import java.util.List;
import java.util.stream.Stream;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

public class FormPanel extends JPanel {


    private FormField<?> lastField = null;

    private final Box buttons = new Box(BoxLayout.X_AXIS);

    private final SpringLayoutHelper helper;

    public FormPanel() {
        helper = SpringLayoutHelper.of(this);
        layoutButtons();
    }

    public FormPanel(List<FormField<?>> formFields, List<JButton> buttons) {
        helper = SpringLayoutHelper.of(this);
        layoutButtons();
        for (FormField<?> formField : formFields) {
            addFormField(formField);
        }
        for (JButton button : buttons) {
            addButton(button);
        }
    }

    private void layoutButtons() {
        add(buttons);

        buttons.add(Box.createHorizontalGlue());
        helper.leftOf(buttons).toLeftOf(this).padding(PADDING);
        helper.rightOf(buttons).toRightOf(this).padding(-PADDING);
    }

    public void addButton(JButton button) {
        buttons.add(Box.createHorizontalStrut(PADDING));
        buttons.add(button);

        helper.bottomOf(this).toBottomOf(buttons).padding(PADDING);
    }

    public void addFormField(FormField<?> formField) {
        add(formField);

        helper.paddingInside(this, formField, PADDING, Edge.LEFT, Edge.RIGHT);

        if (lastField == null) {
            helper.topOf(formField).toTopOf(this).padding(PADDING);
        } else {
            helper.topOf(formField).toBottomOf(lastField).padding(PADDING);
        }

        boolean noButton = Stream.of(this.buttons.getComponents()).noneMatch(c -> c instanceof JButton);
        if (noButton) {
            helper.bottomOf(this).toBottomOf(formField).padding(PADDING);
        }

        helper.topOf(buttons).toBottomOf(formField).padding(PADDING * 2);
        lastField = formField;
    }

    protected void setAutoStretch(FormField<?> formField) {
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

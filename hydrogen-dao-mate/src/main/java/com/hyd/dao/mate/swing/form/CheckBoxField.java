package com.hyd.dao.mate.swing.form;

import java.awt.Component;
import java.util.Collections;
import java.util.List;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;

public class CheckBoxField extends FormField<Boolean> {

    protected final JCheckBox checkBox;

    public CheckBoxField(String labelText) {
        super(labelText);
        checkBox = new JCheckBox("");
        checkBox.setBorder(null);

        Box box = new Box(BoxLayout.X_AXIS);
        box.add(checkBox);
        box.add(Box.createHorizontalGlue());

        add(box);
    }

    @Override
    protected List<Component> getFunctionComponents() {
        return Collections.singletonList(checkBox);
    }

    @Override
    public Boolean getValue() {
        return checkBox.isSelected();
    }

    @Override
    public void setValue(Boolean value) {
        this.checkBox.setSelected(value);
    }
}

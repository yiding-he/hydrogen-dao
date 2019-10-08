package com.hyd.dao.mate.swing;

import javax.swing.*;
import java.awt.*;
import java.util.Collections;
import java.util.List;

public class ComboBoxField extends FormField<String> {

    private final JComboBox<String> comboBox = new JComboBox<>();

    @SuppressWarnings("unchecked")
    public ComboBoxField(String labelText) {
        super(labelText);
        add(comboBox);

        comboBox.addActionListener(event -> {
            if (this.onValueChanged != null) {
                JComboBox<String> c = (JComboBox<String>) event.getSource();
                Object[] selectedObjects = c.getSelectedObjects();
                this.onValueChanged.accept(selectedObjects.length == 0? null: String.valueOf(selectedObjects[0]));
            }
        });
    }

    public JComboBox<String> getComboBox() {
        return comboBox;
    }

    public void addOption(String option) {
        this.comboBox.addItem(option);
    }

    public void setOptions(List<String> options) {
        this.comboBox.removeAllItems();
        options.forEach(this.comboBox::addItem);
    }

    @Override
    public String getValue() {
        Object selectedItem = this.comboBox.getSelectedItem();
        return selectedItem == null ? null : selectedItem.toString();
    }

    @Override
    public void setValue(String value) {
        this.comboBox.setSelectedItem(value);
    }

    public void select(int index) {
        this.comboBox.setSelectedIndex(index);
    }

    @Override
    protected List<Component> getFunctionComponents() {
        return Collections.singletonList(this.comboBox);
    }
}

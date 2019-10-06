package com.hyd.dao.mate.swing;

import javax.swing.*;

public class ComboBoxField extends FormField<String> {

    private final JComboBox<String> comboBox = new JComboBox<>();

    public ComboBoxField(String labelText) {
        super(labelText);
        add(comboBox);
    }

    public JComboBox<String> getComboBox() {
        return comboBox;
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
}

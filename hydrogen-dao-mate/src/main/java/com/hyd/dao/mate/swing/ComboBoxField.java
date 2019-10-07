package com.hyd.dao.mate.swing;

import javax.swing.*;
import java.util.List;

public class ComboBoxField extends FormField<String> {

    private final JComboBox<String> comboBox = new JComboBox<>();

    public ComboBoxField(String labelText) {
        super(labelText);
        add(comboBox);
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

    public void setOnSelectionChanged(Runnable action) {
        this.comboBox.addActionListener(event -> action.run());
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
}

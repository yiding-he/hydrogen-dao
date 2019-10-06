package com.hyd.dao.mate.swing;

import javax.swing.*;

public class ListField extends FormField<String> {

    private final JList<String> list = new JList<>();

    public ListField(String labelText) {
        super(labelText);
        add(new JScrollPane(list));
    }

    @Override
    public String getValue() {
        return this.list.getSelectedValue();
    }

    @Override
    public void setValue(String value) {
        this.list.setSelectedValue(value, true);
    }
}

package com.hyd.dao.mate.swing.form;

import java.awt.Component;
import java.util.Collections;
import java.util.List;
import javax.swing.*;

public class ListField extends FormField<String> {

    private final JList<String> list = new JList<>();

    @SuppressWarnings("unchecked")
    public ListField(String labelText) {
        super(labelText);
        add(new JScrollPane(list));

        list.addListSelectionListener(e -> {
            if (onValueChanged != null) {
                JList<String> l = (JList<String>) e.getSource();
                onValueChanged.accept(l.getSelectedValue());
            }
        });
    }

    @Override
    public String getValue() {
        return this.list.getSelectedValue();
    }

    @Override
    public void setValue(String value) {
        this.list.setSelectedValue(value, true);
    }

    public void setItems(List<String> items) {
        DefaultListModel<String> listModel = new DefaultListModel<>();
        items.forEach(listModel::addElement);
        this.list.setModel(listModel);
    }

    @Override
    protected List<Component> getFunctionComponents() {
        return Collections.singletonList(this.list);
    }
}

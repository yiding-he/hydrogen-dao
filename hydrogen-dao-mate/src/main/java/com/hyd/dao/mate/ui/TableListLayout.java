package com.hyd.dao.mate.ui;

import com.hyd.dao.mate.swing.ComboBoxField;
import com.hyd.dao.mate.swing.FormPanel;
import com.hyd.dao.mate.swing.ListField;

public class TableListLayout extends FormPanel {

    protected final ComboBoxField schemas = new ComboBoxField("Schema");

    protected final ListField tables = new ListField("表");

    public TableListLayout() {
        addFormField(schemas);
        addFormField(tables);
        setAutoStretch(tables);
    }

    public ComboBoxField getSchemas() {
        return schemas;
    }

    public ListField getTables() {
        return tables;
    }
}

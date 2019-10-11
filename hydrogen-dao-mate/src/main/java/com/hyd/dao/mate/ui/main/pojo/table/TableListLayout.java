package com.hyd.dao.mate.ui.main.pojo.table;

import com.hyd.dao.mate.swing.FormPanel;
import com.hyd.dao.mate.swing.form.ComboBoxField;
import com.hyd.dao.mate.swing.form.ListField;

public class TableListLayout extends FormPanel {

    protected final ComboBoxField catalogs = new ComboBoxField("数据库");

    protected final ListField tables = new ListField("表");

    public TableListLayout() {
        addFormField(catalogs);
        addFormField(tables);
        setAutoStretch(tables);
    }

    public ComboBoxField getCatalogs() {
        return catalogs;
    }

    public ListField getTables() {
        return tables;
    }
}

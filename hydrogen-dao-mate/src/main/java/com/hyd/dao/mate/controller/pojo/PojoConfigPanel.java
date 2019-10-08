package com.hyd.dao.mate.controller.pojo;

import com.hyd.dao.database.type.NameConverter;
import com.hyd.dao.mate.CodeMateMain;
import com.hyd.dao.mate.ui.pojo.PojoConfigLayout;
import com.hyd.dao.mate.util.Events;
import com.hyd.dao.mate.util.Listeners;
import com.hyd.dao.mate.util.Str;

public class PojoConfigPanel extends PojoConfigLayout {

    public PojoConfigPanel() {
        pojoName.setEnabled(false);

        Listeners.addListener(Events.SelectedTableChanged, () -> {
            String tableName = CodeMateMain
                .getMainFrame().getCreatePojoPanel()
                .getTableListPanel().getTables().getValue();

            if (tableName == null) {
                reset();
            } else {
                pojoName.setValue(toClassName(tableName));
                pojoName.setEnabled(true);
            }
        });
    }

    private String toClassName(String tableName) {
        String s = NameConverter.CAMEL_UNDERSCORE.column2Field(tableName);
        return Str.capitalize(s);
    }

    public void reset() {
        pojoName.setValue("");
        pojoName.setEnabled(false);
    }
}

package com.hyd.dao.mate.ui.main.pojo.generate;

import com.hyd.dao.database.type.NameConverter;
import com.hyd.dao.mate.CodeMateMain;
import com.hyd.dao.mate.generator.PojoGenerator;
import com.hyd.dao.mate.swing.Swing;
import com.hyd.dao.mate.ui.result.PojoResultFrame;
import com.hyd.dao.mate.util.*;

public class PojoConfigPanel extends PojoConfigLayout {

    public PojoConfigPanel() {
        reset();

        Listeners.addListener(Events.SelectedTableChanged, () -> {
            String tableName = getTableName();

            if (tableName == null) {
                reset();
            } else {
                pojoName.setValue(toClassName(tableName));
                pojoName.setEnabled(true);
                generateButton.setEnabled(true);
            }
        });

        generateButton.addActionListener(event -> generateCode());
    }

    private String getTableName() {
        return CodeMateMain
            .getMainFrame().getCreatePojoPanel().getTableListPanel()
            .getTables().getValue();
    }

    private String getCatalog() {
        return CodeMateMain
            .getMainFrame().getCreatePojoPanel().getTableListPanel()
            .getCatalogs().getValue();
    }

    private String toClassName(String tableName) {
        String s = NameConverter.CAMEL_UNDERSCORE.column2Field(tableName);
        return Str.capitalize(s);
    }

    public void reset() {
        pojoName.setValue("");
        pojoName.setEnabled(false);
        generateButton.setEnabled(false);
    }

    private void generateCode() {

        PojoGenerator generator = new PojoGenerator();
        generator.setConnection(CodeMateMain.getMainFrame().getConnection());
        generator.setCatalog(getCatalog());
        generator.setTableName(this.getTableName());
        generator.setPojoName(this.pojoName.getValue());

        String pojoCode = generator.generateCode();
        PojoResultFrame frame = new PojoResultFrame(
            this.pojoName.getValue(),
            pojoCode
        );

        Swing.openSubWindow(frame, 500, 700);
    }
}

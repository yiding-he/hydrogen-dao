package com.hyd.dao.mate.ui.main.pojo.generate;

import static com.hyd.dao.mate.swing.Swing.disableComponents;
import static com.hyd.dao.mate.swing.Swing.enableComponents;

import com.hyd.dao.database.type.NameConverter;
import com.hyd.dao.mate.CodeMateMain;
import com.hyd.dao.mate.generator.PojoGenerator;
import com.hyd.dao.mate.swing.Swing;
import com.hyd.dao.mate.ui.result.PojoResultFrame;
import com.hyd.dao.mate.util.Events;
import com.hyd.dao.mate.util.Listeners;
import com.hyd.dao.mate.util.Str;
import java.awt.Cursor;
import java.sql.SQLException;

public class PojoConfigPanel extends PojoConfigLayout {

    public PojoConfigPanel() {
        reset();

        this.convertType.addOption("字段名下划线分隔");
        this.convertType.addOption("不转换");
        this.convertType.select(0);

        Listeners.addListener(Events.SelectedTableChanged, () -> {
            String tableName = getTableName();

            if (tableName == null) {
                reset();
            } else {
                pojoName.setValue(toClassName(tableName));
                enableComponents(pojoName, generateButton, convertType, lombok, mybatisPlus);
            }
        });

        generateButton.addActionListener(event -> {
            generateButton.setEnabled(false);
            generateButton.setText("请稍候...");
            generateButton.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            new Thread(this::generateCode).start();
        });
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

    private void reset() {
        pojoName.setValue("");
        disableComponents(pojoName, generateButton, convertType, lombok, mybatisPlus);
    }

    private void generateCode() {
        try {
            NameConverter nameConverter = convertType.getComboBox().getSelectedIndex() == 0 ?
                NameConverter.CAMEL_UNDERSCORE : NameConverter.NONE;

            PojoGenerator generator = new PojoGenerator();
            generator.setConnection(CodeMateMain.getMainFrame().getConnection());
            generator.setCatalog(getCatalog());
            generator.setTableName(this.getTableName());
            generator.setPojoName(this.pojoName.getValue());
            generator.setNameConverter(nameConverter);
            generator.setUseLombok(this.lombok.getValue());
            generator.setUseMybatisPlus(this.mybatisPlus.getValue());

            String pojoCode = generator.generateCode();
            PojoResultFrame frame = new PojoResultFrame(
                this.pojoName.getValue(),
                pojoCode
            );

            Swing.openSubWindow(frame, 500, 700);
        } catch (SQLException e) {
            e.printStackTrace();
            Swing.alertError("错误", e.toString());
        } finally {
            generateButton.setEnabled(true);
            generateButton.setText("生成代码");
            generateButton.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
    }
}

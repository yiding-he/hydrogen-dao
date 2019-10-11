package com.hyd.dao.mate.ui.main.pojo.table;

import com.hyd.dao.Row;
import com.hyd.dao.mate.CodeMateMain;
import com.hyd.dao.mate.util.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.List;
import java.util.stream.Collectors;

public class TableListPanel extends TableListLayout {

    public TableListPanel() {
        this.tables.setEnabled(false);
        this.catalogs.setEnabled(false);

        Listeners.addListener(Events.DatabaseConnected, () -> {
            this.tables.setEnabled(true);
            this.catalogs.setEnabled(true);
        });

        this.tables.setOnValueChanged(tableName -> Listeners.publish(Events.SelectedTableChanged));

        this.catalogs.setOnValueChanged(this::loadTables);
    }

    private void loadTables(String schema) {
        try {
            Connection connection = CodeMateMain.getMainFrame().getConnection();
            ResultSet tablesResultSet = connection.getMetaData().getTables(schema, schema, "%", null);
            List<Row> tables = ResultSetUtil.readResultSet(tablesResultSet);

            List<String> tableNames = tables.stream()
                .map(row -> row.getString("TABLE_NAME")).collect(Collectors.toList());

            this.tables.setItems(tableNames);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void reset() {
        try {
            Connection connection = CodeMateMain.getMainFrame().getConnection();
            List<Row> schemas = ResultSetUtil.readResultSet(connection.getMetaData().getCatalogs());
            schemas.forEach(row -> this.catalogs.addOption(row.getString("TABLE_CAT")));
            this.catalogs.select(0);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

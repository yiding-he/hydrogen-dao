package com.hyd.dao.mate.controller;

import com.hyd.dao.Row;
import com.hyd.dao.mate.CodeMateMain;
import com.hyd.dao.mate.ui.TableListLayout;
import com.hyd.dao.mate.util.ResultSetUtil;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.List;
import java.util.stream.Collectors;

public class TableListPanel extends TableListLayout {

    public TableListPanel() {
        this.schemas.setOnSelectionChanged(() -> {
            String schema = this.schemas.getValue();
            loadTables(schema);
        });
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
            schemas.forEach(row -> this.schemas.addOption(row.getString("TABLE_CAT")));
            this.schemas.select(0);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

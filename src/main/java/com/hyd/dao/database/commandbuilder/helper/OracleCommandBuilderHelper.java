package com.hyd.dao.database.commandbuilder.helper;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author hyd
 */
public class OracleCommandBuilderHelper extends CommandBuilderHelper {

    public OracleCommandBuilderHelper(Connection connection) {
        super(connection);
        this.columnMeta = ColumnMeta.Oracle;
    }

    @Override
    protected String fixTableName(String tableName) {
        return tableName.toUpperCase();
    }

    @Override
    public String getColumnName(String column) throws SQLException {
        return "\"" + column.toUpperCase() + "\"";
    }

    @Override
    public String getSysdateMark() {
        return "sysdate";
    }
}

package com.hyd.dao.database.commandbuilder.helper;

import com.hyd.dao.database.executor.ExecutionContext;
import java.sql.SQLException;

/**
 * (description)
 *
 * @author yiding.he
 */
public class MySqlCommandBuilderHelper extends CommandBuilderHelper {

    public MySqlCommandBuilderHelper(ExecutionContext context) {
        super(context);
    }

    @Override
    protected ColumnMeta getColumnMeta() {
        return ColumnMeta.MySQL;
    }

    @Override
    public String getRangedSql(String sql, int startPos, int endPos) {
        int size = endPos - startPos;
        return "select range_wrapper.* from (" + sql + ") range_wrapper limit " + startPos + ", " + size;
    }

    @Override
    protected String getCatalog() throws SQLException {
        String catalog = super.getCatalog();
        return catalog == null || catalog.isEmpty() ? null : catalog;
    }

    @Override
    public String getTableNameForSql(String tableName) {
        return "`" + tableName + "`";
    }

    @Override
    public String getColumnNameForSql(String colName) {
        return "`" + colName + "`";
    }

    @Override
    public String getCountSql(String sql) {
        return "select count(*) cnt from (" + sql + ") count_sql_wrapper";
    }
}

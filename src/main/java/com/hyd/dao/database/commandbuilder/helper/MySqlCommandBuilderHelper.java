package com.hyd.dao.database.commandbuilder.helper;

import java.sql.Connection;

/**
 * (description)
 *
 * @author yiding.he
 */
public class MySqlCommandBuilderHelper extends CommandBuilderHelper {

    public MySqlCommandBuilderHelper(Connection conn) {
        super(conn);
        this.columnMeta = ColumnMeta.MySQL;
    }

    @Override
    public String getRangedSql(String sql, int startPos, int endPos) {
        int size = endPos - startPos;
        return "select range_wrapper.* from (" + sql + ") range_wrapper limit " + startPos + ", " + size;
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

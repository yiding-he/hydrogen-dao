package com.hyd.dao.command.builder.helper;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * (description)
 *
 * @author yiding.he
 */
public class MySqlSqlFix implements SqlFix {


    @Override
    public ColumnMeta getColumnMeta() {
        return ColumnMeta.MySQL;
    }

    @Override
    public String getRangedSql(String sql, int startPos, int endPos) {
        int size = endPos - startPos;
        return "select range_wrapper.* from (" + sql + ") range_wrapper limit " + startPos + ", " + size;
    }

    @Override
    public String getCatalog(Connection connection) throws SQLException {
        String catalog = SqlFix.super.getCatalog(connection);
        return catalog == null || catalog.isEmpty() ? null : catalog;
    }

    @Override
    public String getTableNameForSql(String tableName) {
        return "`" + tableName + "`";
    }

    @Override
    public String getStrictName(String name) {
        return "`" + name + "`";
    }

    @Override
    public String getCountSql(String sql) {
        return "select count(*) cnt from (" + sql + ") count_sql_wrapper";
    }
}

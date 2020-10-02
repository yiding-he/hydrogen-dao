package com.hyd.dao.command.builder.helper;

import com.hyd.dao.DAOException;

import java.sql.Connection;
import java.sql.SQLException;

public interface SqlFix {

    SqlFix DEFAULT = new DefaultSqlFix();

    default ColumnMeta getColumnMeta() {
        return ColumnMeta.Oracle;
    }

    default String getSchema(String schema) {
        return schema;
    }

    default String getCatalog(Connection connection) throws SQLException {
        return connection.getCatalog();
    }

    // 当查询 meta 数据需要时，修正表名
    default String getTableNameForMeta(String tableName) {
        return tableName;
    }

    // 当组合 SQL 语句需要时，修正表名
    default String getTableNameForSql(String tableName) {
        return tableName;
    }

    /**
     * Fix name in the sql statement.
     *
     * @param name name
     *
     * @return fixed name
     * @throws DAOException when fails
     */
    default String getStrictName(String name) throws DAOException {
        return name;
    }

    default String getSysdateMark() {
        return "CURRENT_TIMESTAMP";
    }

    // 根据当前的 SQL 语句生成带查询范围的语句
    default String getRangedSql(String sql, int startPos, int endPos) {
        return null;
    }

    // 根据当前的 SQL 语句生成返回查询结果数量的语句
    default String getCountSql(String sql) {
        return "select count(*) cnt from (" + sql + ")";
    }

}

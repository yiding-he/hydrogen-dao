package com.hyd.dao.command.builder.helper;

import java.sql.Connection;

/**
 * (description)
 * created at 2015/2/10
 *
 * @author Yiding
 */
public class HsqldbSqlFix implements SqlFix {

    @Override
    public String getSchema(String schema) {
        return "PUBLIC";
    }

    @Override
    public String getTableNameForMeta(String tableName) {
        return tableName.toUpperCase();
    }

    @Override
    public String getCatalog(Connection connection) {
        return null;
    }

    @Override
    public String getSysdateMark() {
        return "CURRENT_DATE";
    }

    @Override
    public String getRangedSql(String sql, int startPos, int endPos) {
        int size = endPos - startPos;
        return "select range_wrapper.* from (" + sql + ") range_wrapper " +
            " limit " + startPos + "," + size;
    }
}

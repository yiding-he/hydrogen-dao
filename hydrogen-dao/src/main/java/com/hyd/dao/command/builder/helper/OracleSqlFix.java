package com.hyd.dao.command.builder.helper;

import com.hyd.dao.mate.util.ResultSetUtil;

/**
 * @author hyd
 */
public class OracleSqlFix implements SqlFix {

    @Override
    public String getTableNameForMeta(String tableName) {
        return tableName.toUpperCase();
    }

    @Override
    public String getStrictName(String name) {
        return "\"" + name.toUpperCase() + "\"";
    }

    @Override
    public String getSysdateMark() {
        return "sysdate";
    }

    @Override
    public String getRangedSql(String sql, int startPos, int endPos) {
        int _startPos = startPos + 1;
        String sql_prefix = "select * from ( select pagnation_wrapper.*, rownum " +
                ResultSetUtil.PAGINATION_WRAPPER_COLUMN_NAME + " from (";
        String sql_suffix = ") pagnation_wrapper) where " +
                ResultSetUtil.PAGINATION_WRAPPER_COLUMN_NAME + " between " + _startPos + " and " + endPos;
        return sql_prefix + sql + sql_suffix;
    }
}

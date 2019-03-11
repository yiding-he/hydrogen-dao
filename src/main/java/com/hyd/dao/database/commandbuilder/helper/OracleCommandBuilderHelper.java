package com.hyd.dao.database.commandbuilder.helper;

import com.hyd.dao.util.ResultSetUtil;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author hyd
 */
public class OracleCommandBuilderHelper extends CommandBuilderHelper {

    public OracleCommandBuilderHelper(Connection connection) {
        super(connection);
    }

    @Override
    protected String getTableNameForMeta(String tableName) {
        return tableName.toUpperCase();
    }

    @Override
    public String getColumnNameForSql(String column) throws SQLException {
        return "\"" + column.toUpperCase() + "\"";
    }

    @Override
    public String getSysdateMark() {
        return "sysdate";
    }

    @Override
    public String getRangedSql(String sql, int startPos, int endPos) {
        startPos += 1;

        String sql_prefix = "select * from ( select pagnation_wrapper.*, rownum " +
                ResultSetUtil.PAGINATION_WRAPPER_COLUMN_NAME + " from (";
        String sql_suffix = ") pagnation_wrapper) where " +
                ResultSetUtil.PAGINATION_WRAPPER_COLUMN_NAME + " between " + startPos + " and " + endPos;
        return sql_prefix + sql + sql_suffix;
    }
}

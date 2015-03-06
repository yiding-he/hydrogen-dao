package com.hyd.dao.database.executor;

import com.hyd.dao.util.ResultSetUtil;

import java.sql.Connection;

/**
 * (description)
 *
 * @author yiding.he
 */
public class OracleExecutor extends DefaultExecutor {

    /**
     * 构造函数
     *
     * @param connection 数据库连接
     */
    public OracleExecutor(Connection connection) {
        super(connection);
    }

    @Override
    protected String getRangedSql(String sql, int startPos, int endPos) {
        startPos += 1;

        String sql_prefix = "select * from ( select pagnation_wrapper.*, rownum " +
                ResultSetUtil.PAGNATION_WRAPPER_COLUMN_NAME + " from (";
        String sql_suffix = ") pagnation_wrapper) where " +
                ResultSetUtil.PAGNATION_WRAPPER_COLUMN_NAME + " between " + startPos + " and " + endPos;
        return sql_prefix + sql + sql_suffix;
    }

    @Override
    protected String getCountSql(String sql) {
        return "select count(*) cnt from (" + sql + ")";
    }
}

package com.hyd.dao.database.executor;

import java.sql.Connection;

/**
 * (description)
 *
 * @author yiding.he
 */
public class MysqlExecutor extends DefaultExecutor {

    /**
     * 构造函数
     *
     * @param connection 数据库连接
     */
    public MysqlExecutor(Connection connection) {
        super(connection);
    }

    @Override
    protected String getRangedSql(String sql, int startPos, int endPos) {
        int size = endPos - startPos;
        return "select a.* from (" + sql + ") a limit " + startPos + ", " + size;
    }

    @Override
    protected String getCountSql(String sql) {
        return "select count(*) cnt from (" + sql + ") a";
    }
}

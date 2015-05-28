package com.hyd.dao.database.commandbuilder.helper;

import java.sql.Connection;

/**
 * (description)
 * created at 2015/5/16
 *
 * @author Yiding
 */
public class SQLServerCommandBuilderHelper extends CommandBuilderHelper {

    /**
     * 构造函数
     *
     * @param connection 数据库连接
     */
    protected SQLServerCommandBuilderHelper(Connection connection) {
        super(connection);
    }

    @Override
    protected String getSchema(String schema) {
        return "%";
    }
}

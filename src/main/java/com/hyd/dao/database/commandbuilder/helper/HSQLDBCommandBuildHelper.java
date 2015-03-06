package com.hyd.dao.database.commandbuilder.helper;

import java.sql.Connection;

/**
 * (description)
 * created at 2015/2/10
 *
 * @author Yiding
 */
public class HSQLDBCommandBuildHelper extends CommandBuilderHelper {

    /**
     * 构造函数
     *
     * @param connection 数据库连接
     */
    protected HSQLDBCommandBuildHelper(Connection connection) {
        super(connection);
    }

    @Override
    protected String getSchema(String schema) {
        return "PUBLIC";
    }

    @Override
    protected String getTableName(String tableName) {
        return tableName.toUpperCase();
    }
}

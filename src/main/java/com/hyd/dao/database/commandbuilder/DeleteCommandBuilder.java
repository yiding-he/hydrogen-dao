package com.hyd.dao.database.commandbuilder;

import com.hyd.dao.database.ColumnInfo;
import com.hyd.dao.database.commandbuilder.helper.CommandBuilderHelper;
import com.hyd.dao.database.executor.ExecutionContext;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 生成 update 语句
 */
public class DeleteCommandBuilder {

    private DeleteCommandBuilder() {

    }

    /**
     * 根据 object 构造参数
     */
    public static Command build(ExecutionContext context, String tableName, Object object) throws SQLException {
        FQN fqn = new FQN(context.getConnection(), tableName);
        final CommandBuilderHelper helper = CommandBuilderHelper.getHelper(context);
        ColumnInfo[] infos = helper.getColumnInfos(fqn.getSchema("%"), fqn.getName());

        String command = "delete from " + tableName;
        StringBuilder whereMarks = new StringBuilder();
        List<Object> whereParams = new ArrayList<>();

        for (ColumnInfo info : infos) {
            if (info.isPrimary()) {
                whereMarks.append(helper.getColumnNameForSql(info.getColumnName())).append("=? and");
                whereParams.add(helper.generateParamValue(object, info));
            }
        }

        if ("".equals(whereMarks.toString())) {
            throw new NoPrimaryKeyException("no primary key found in table \"" + tableName + "\"");
        }

        if (whereMarks.toString().endsWith("and")) {
            whereMarks = new StringBuilder(whereMarks.substring(0, whereMarks.length() - 3));
        }

        command += " where " + whereMarks;
        return new Command(command, whereParams);

    }

    /**
     * 根据主键值构造参数
     */
    public static Command buildByKey(ExecutionContext context, String tableName, Object key) throws SQLException {

        Connection connection = context.getConnection();
        FQN fqn = new FQN(connection, tableName);
        CommandBuilderHelper helper = CommandBuilderHelper.getHelper(context);
        ColumnInfo[] infos = helper.getColumnInfos(fqn.getSchema("%"), fqn.getName());

        String statement = "delete from " + tableName + " where ";
        for (ColumnInfo info : infos) {
            if (info.isPrimary()) {
                statement += helper.getColumnNameForSql(info.getColumnName()) + "=?";
                break;
            }
        }

        List<Object> params = new ArrayList<>();
        params.add(key);
        return new Command(statement, params);
    }
}

package com.hyd.dao.command.builder;

import com.hyd.dao.DAOException;
import com.hyd.dao.command.Command;
import com.hyd.dao.command.builder.helper.CommandBuilderHelper;
import com.hyd.dao.database.ColumnInfo;
import com.hyd.dao.database.FQN;
import com.hyd.dao.database.executor.ExecutionContext;
import com.hyd.dao.exception.NoPrimaryKeyException;

import java.util.ArrayList;
import java.util.List;

/**
 * 生成 update 语句
 */
public class DeleteCommandBuilder {

    private DeleteCommandBuilder() {

    }

    /**
     * 从 object 中提取主键值作为参数
     */
    public static Command build(String tableName, Object object) throws DAOException {
        ExecutionContext context = ExecutionContext.get();
        FQN fqn = new FQN(context, tableName);
        final CommandBuilderHelper helper = CommandBuilderHelper.getHelper();
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
    public static Command buildByKey(String tableName, Object key) throws DAOException {
        ExecutionContext context = ExecutionContext.get();
        FQN fqn = new FQN(context, tableName);
        CommandBuilderHelper helper = CommandBuilderHelper.getHelper();
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

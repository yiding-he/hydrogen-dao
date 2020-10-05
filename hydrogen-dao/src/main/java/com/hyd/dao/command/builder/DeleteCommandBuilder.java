package com.hyd.dao.command.builder;

import com.hyd.dao.DAOException;
import com.hyd.dao.command.Command;
import com.hyd.dao.command.builder.helper.CommandBuilderHelper;
import com.hyd.dao.database.ColumnInfo;
import com.hyd.dao.database.FQN;
import com.hyd.dao.mate.util.ConnectionContext;

import java.util.ArrayList;
import java.util.List;

/**
 * 生成 delete 语句
 */
public final class DeleteCommandBuilder extends CommandBuilder {

    public DeleteCommandBuilder(ConnectionContext context) {
        super(context);
    }

    /**
     * 从 object 中提取参数
     */
    public Command build(String tableName, Object object) throws DAOException {
        if (object == null) {
            throw new NullPointerException("object is null");
        }

        final FQN fqn = new FQN(context, tableName);
        final CommandBuilderHelper helper = CommandBuilderHelper.getHelper(context);
        final ColumnInfo[] infos = helper.getColumnInfos(fqn.getSchema("%"), fqn.getName());

        final String command = "delete from " + fqn.getStrictName() + " where ";
        final List<String> whereStatements = new ArrayList<>();
        final List<Object> whereParams = new ArrayList<>();

        for (ColumnInfo info : infos) {
            Object param = helper.generateParamValue(object, info);
            if (param != null) {
                whereParams.add(param);
                whereStatements.add(helper.getStrictName(info.getColumnName()) + "=?");
            }
        }

        if (whereStatements.isEmpty()) {
            throw new IllegalStateException(
                "Delete command has no condition, dangerous operation prohibited.");
        }

        return new Command(command + String.join(" and ", whereStatements), whereParams);
    }

    /**
     * 根据主键值构造参数
     */
    public Command buildByKey(String tableName, Object key) throws DAOException {
        FQN fqn = new FQN(context, tableName);
        CommandBuilderHelper helper = CommandBuilderHelper.getHelper(context);
        ColumnInfo[] infos = helper.getColumnInfos(fqn.getSchema("%"), fqn.getName());

        String statement = "delete from " + tableName + " where ";
        for (ColumnInfo info : infos) {
            if (info.isPrimary()) {
                statement += helper.getStrictName(info.getColumnName()) + "=?";
                break;
            }
        }

        List<Object> params = new ArrayList<>();
        params.add(key);
        return new Command(statement, params);
    }
}

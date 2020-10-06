package com.hyd.dao.command.builder;

import com.hyd.dao.DAOException;
import com.hyd.dao.SQL;
import com.hyd.dao.command.Command;
import com.hyd.dao.command.builder.helper.CommandBuilderHelper;
import com.hyd.dao.database.ColumnInfo;
import com.hyd.dao.database.FQN;
import com.hyd.dao.mate.util.ConnectionContext;

/**
 * 生成 delete 语句
 */
public final class DeleteBuilder extends CommandBuilder {

    public DeleteBuilder(ConnectionContext context) {
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

        SQL.Delete delete = new SQL.Delete(fqn.getStrictName());
        for (ColumnInfo info : infos) {
            String columnName = helper.getStrictName(info.getColumnName());
            Object param = helper.generateParamValue(object, info);
            if (param != null) {
                delete.Where(columnName + "=?", param);
            }
        }

        if (!delete.hasConditions()) {
            throw new IllegalStateException(
                "Delete command has no condition, dangerous operation prohibited.");
        }

        return delete.toCommand();
    }

    /**
     * 根据主键值构造参数（仅支持单个字段主键）
     */
    public Command buildByKey(String tableName, Object key) throws DAOException {
        if (key == null) {
            throw new NullPointerException("key is null");
        }

        FQN fqn = new FQN(context, tableName);
        CommandBuilderHelper helper = CommandBuilderHelper.getHelper(context);
        ColumnInfo[] infos = helper.getColumnInfos(fqn.getSchema("%"), fqn.getName());

        SQL.Delete delete = new SQL.Delete(fqn.getStrictName());
        for (ColumnInfo info : infos) {
            if (info.isPrimary()) {
                String columnName = helper.getStrictName(info.getColumnName());
                delete.Where(columnName + "=?", key);
                break;
            }
        }

        return delete.toCommand();
    }
}

package com.hyd.dao.command.builder;

import com.hyd.dao.DAOException;
import com.hyd.dao.SQL;
import com.hyd.dao.command.Command;
import com.hyd.dao.database.ColumnInfo;
import com.hyd.dao.database.ConnectionContext;
import com.hyd.dao.database.FQN;
import com.hyd.dao.database.dialects.Dialect;
import com.hyd.dao.database.type.NameConverter;

import java.util.List;

import static com.hyd.dao.command.builder.helper.CommandBuilderHelper.generateParamValue;
import static com.hyd.dao.command.builder.helper.CommandBuilderHelper.getColumnInfos;

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
        final NameConverter nameConverter = context.getNameConverter();
        final List<ColumnInfo> infos = getColumnInfos(fqn, context);
        final Dialect dialect = context.getDialect();

        SQL.Delete delete = new SQL.Delete(fqn.getQuotedName());
        for (ColumnInfo info : infos) {
            String columnName = dialect.quote(info.getColumnName());
            Object param = generateParamValue(object, info, nameConverter);
            if (param != null) {
                delete.And(columnName + "=?", param);
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

        final FQN fqn = new FQN(context, tableName);
        final List<ColumnInfo> infos = getColumnInfos(fqn, context);
        final Dialect dialect = context.getDialect();

        SQL.Delete delete = new SQL.Delete(fqn.getQuotedName());
        for (ColumnInfo info : infos) {
            if (info.isPrimary()) {
                String columnName = dialect.quote(info.getColumnName());
                delete.Where(columnName + "=?", key);
                break;
            }
        }

        return delete.toCommand();
    }
}

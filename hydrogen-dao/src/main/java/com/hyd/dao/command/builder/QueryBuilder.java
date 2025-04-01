package com.hyd.dao.command.builder;

import com.hyd.dao.DAOException;
import com.hyd.dao.SQL;
import com.hyd.dao.command.Command;
import com.hyd.dao.database.ColumnInfo;
import com.hyd.dao.database.ConnectionContext;
import com.hyd.dao.database.FQN;
import com.hyd.dao.database.type.NameConverter;
import com.hyd.dao.etc.Nullable;

import java.util.List;

import static com.hyd.dao.command.builder.helper.CommandBuilderHelper.*;

/**
 * 构建查询语句的类
 *
 * @author yiding.he
 */
public final class QueryBuilder extends CommandBuilder {

    public QueryBuilder(ConnectionContext context) {
        super(context);
    }

    /**
     * 根据主键值构建查询语句
     */
    public Command buildByKey(String tableName, Object primaryKey) throws DAOException {
        final FQN fqn = new FQN(context, tableName);
        final List<ColumnInfo> infos = getColumnInfos(fqn, context);
        final SQL.Select select = new SQL.Select("*").From(fqn.getQuotedName());

        for (ColumnInfo info : infos) {
            if (info.isPrimary()) {
                select.And(context.getDialect().quote(info.getColumnName()) + "=?", primaryKey);
                break;
            }
        }

        if (!select.hasConditions()) {
            throw new DAOException("Primary key not found in table \"" + tableName + "\"");
        }

        return select.toCommand();
    }

    /**
     * 根据 obj 对象构建查询语句
     *
     * @param tableName 表名
     * @param obj       对象, 如果为 null, 则查询所有字段，否则：
     *                  1. 根据对象属性和实际表字段决定查询哪些字段；
     *                  2. 根据对象当中不为空的属性值构建查询条件。
     */
    public Command build(String tableName, @Nullable Object obj) {
        final FQN fqn = new FQN(context, tableName);
        final SQL.Select select = new SQL.Select("*").From(fqn.getQuotedName());

        final NameConverter nameConverter = context.getNameConverter();
        final List<ColumnInfo> infos = obj == null ?
            getColumnInfos(fqn, context) :
            filterColumnsByType(getColumnInfos(fqn, context), obj.getClass(), nameConverter);

        if (obj != null) {
            infos.forEach(info -> {
                Object value = generateParamValue(obj, info, nameConverter);
                if (value != null) {
                    select.And(context.getDialect().quote(info.getColumnName()) + "=?", value);
                }
            });
        }

        return select.toCommand();
    }
}

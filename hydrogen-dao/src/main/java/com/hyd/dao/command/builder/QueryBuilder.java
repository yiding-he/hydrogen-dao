package com.hyd.dao.command.builder;

import com.hyd.dao.DAOException;
import com.hyd.dao.SQL;
import com.hyd.dao.command.Command;
import com.hyd.dao.command.builder.helper.CommandBuilderHelper;
import com.hyd.dao.database.ColumnInfo;
import com.hyd.dao.database.FQN;
import com.hyd.dao.mate.util.ConnectionContext;

import java.util.Arrays;

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
        final CommandBuilderHelper helper = CommandBuilderHelper.getHelper(context);
        final ColumnInfo[] infos = helper.getColumnInfos(fqn);
        final SQL.Select select = new SQL.Select("*").From(fqn.getStrictName());

        for (ColumnInfo info : infos) {
            if (info.isPrimary()) {
                select.And(helper.getStrictName(info.getColumnName()) + "=?", primaryKey);
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
     */
    public Command build(String tableName, Object obj) {
        final FQN fqn = new FQN(context, tableName);
        final CommandBuilderHelper helper = CommandBuilderHelper.getHelper(context);
        final SQL.Select select = new SQL.Select("*").From(fqn.getStrictName());

        final ColumnInfo[] infos = obj == null ?
            helper.getColumnInfos(fqn) :
            helper.filterColumnsByType(helper.getColumnInfos(fqn), obj.getClass());

        if (obj != null) {
            Arrays.stream(infos).forEach(info -> {
                Object value = helper.generateParamValue(obj, info);
                if (value != null) {
                    select.And(helper.getStrictName(info.getColumnName()) + "=?", value);
                }
            });
        }

        return select.toCommand();
    }
}

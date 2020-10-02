package com.hyd.dao.command.builder;

import com.hyd.dao.DAOException;
import com.hyd.dao.command.Command;
import com.hyd.dao.command.builder.helper.CommandBuilderHelper;
import com.hyd.dao.database.ColumnInfo;
import com.hyd.dao.database.FQN;
import com.hyd.dao.database.type.NameConverter;
import com.hyd.dao.mate.util.BeanUtil;
import com.hyd.dao.mate.util.ConnectionContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 构建查询语句的类
 *
 * @author yiding.he
 */
public final class QueryCommandBuilder {

    private final ConnectionContext context;

    public QueryCommandBuilder(ConnectionContext context) {
        this.context = context;
    }

    /**
     * 根据主键值构建查询语句
     */
    public Command buildByKey(String tableName, Object key) throws DAOException {
        FQN fqn = new FQN(context, tableName);
        final CommandBuilderHelper helper = CommandBuilderHelper.getHelper(context);
        ColumnInfo[] infos = helper.getColumnInfos(fqn.getSchema("%"), fqn.getName());

        String statement = "select * from " + tableName + " where ";
        boolean primaryFound = false;

        for (ColumnInfo info : infos) {
            if (info.isPrimary()) {
                statement += helper.getStrictName(info.getColumnName()) + "=?";
                primaryFound = true;
                break;
            }
        }

        if (!primaryFound) {
            throw new DAOException("Primary key not found in table \"" + tableName + "\"");
        }

        List<Object> values = new ArrayList<>();
        values.add(key);
        return new Command(statement, values);
    }

    /**
     * 根据 obj 对象构建查询语句
     */
    public Command build(String tableName, Object obj) {
        FQN fqn = new FQN(context, tableName);
        NameConverter nameConverter = context.getNameConverter();
        CommandBuilderHelper helper = CommandBuilderHelper.getHelper(context);
        ColumnInfo[] infos = helper.getColumnInfos(fqn.getSchema("%"), fqn.getName());

        List<Object> values = new ArrayList<>();
        StringBuilder statement = new StringBuilder("select * from " + tableName);

        if (obj != null) {
            StringBuilder where = new StringBuilder();

            Arrays.stream(infos)
                .forEach(info -> {
                    String fieldName = nameConverter.column2Field(info.getColumnName());
                    Object value = BeanUtil.getValue(obj, fieldName);

                    if (value != null) {
                        values.add(value);
                        String columnName = helper.getStrictName(info.getColumnName());
                        where.append(columnName).append("=?");
                    }
                });

            if (!values.isEmpty()) {
                statement.append(" where ").append(where);
            }
        }

        return new Command(statement.toString(), values);
    }
}

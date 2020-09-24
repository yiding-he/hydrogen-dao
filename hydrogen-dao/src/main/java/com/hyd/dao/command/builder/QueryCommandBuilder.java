package com.hyd.dao.command.builder;

import com.hyd.dao.DAOException;
import com.hyd.dao.command.Command;
import com.hyd.dao.command.builder.helper.CommandBuilderHelper;
import com.hyd.dao.database.ColumnInfo;
import com.hyd.dao.database.FQN;
import com.hyd.dao.database.executor.ExecutionContext;
import com.hyd.dao.database.type.NameConverter;
import com.hyd.dao.mate.util.BeanUtil;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 构建查询语句的类
 *
 * @author yiding.he
 */
@SuppressWarnings({"unchecked"})
public class QueryCommandBuilder {

    /**
     * 根据主键值构建查询语句
     */
    public static Command buildByKey(String tableName, Object key) throws DAOException {
        ExecutionContext context = ExecutionContext.get();
        FQN fqn = new FQN(context, tableName);
        final CommandBuilderHelper helper = CommandBuilderHelper.getHelper();
        ColumnInfo[] infos = helper.getColumnInfos(fqn.getSchema("%"), fqn.getName());

        String statement = "select * from " + tableName + " where ";
        boolean primaryFound = false;

        for (ColumnInfo info : infos) {
            if (info.isPrimary()) {
                statement += helper.getColumnNameForSql(info.getColumnName()) + "=?";
                primaryFound = true;
                break;
            }
        }

        if (!primaryFound) {
            throw new DAOException("Primary key not found in table \"" + tableName + "\"");
        }

        List values = new ArrayList();
        values.add(key);
        return new Command(statement, values);
    }

    /**
     * 根据 obj 对象构建查询语句
     */
    public static Command build(String tableName, Object obj) throws SQLException {
        ExecutionContext context = ExecutionContext.get();
        FQN fqn = new FQN(context, tableName);
        NameConverter nameConverter = context.getNameConverter();
        final CommandBuilderHelper helper = CommandBuilderHelper.getHelper();
        ColumnInfo[] infos = helper.getColumnInfos(fqn.getSchema("%"), fqn.getName());

        List values = new ArrayList();
        String statement = "select * from " + tableName + " where ";
        for (ColumnInfo info : infos) {
            if (info.isPrimary()) {
                statement += helper.getColumnNameForSql(info.getColumnName()) + "=?";
                String fieldName = nameConverter.column2Field(info.getColumnName());
                values.add(BeanUtil.getValue(obj, fieldName));
                break;
            }
        }

        return new Command(statement, values);
    }
}

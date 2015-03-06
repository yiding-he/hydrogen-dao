package com.hyd.dao.database.commandbuilder;

import com.hyd.dao.DAOException;
import com.hyd.dao.database.ColumnInfo;
import com.hyd.dao.database.commandbuilder.helper.CommandBuilderHelper;
import com.hyd.dao.database.type.TypeConverter;
import com.hyd.dao.util.BeanUtil;

import java.sql.Connection;
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
     *
     * @param connection 数据库连接
     * @param tableName  表名
     * @param key        主键值
     *
     * @return 查询语句
     *
     * @throws SQLException 如果获取数据库连接信息失败
     */
    public static Command buildByKey(Connection connection, String tableName, Object key) throws SQLException {
        FQN fqn = new FQN(connection, tableName);
        ColumnInfo[] infos = CommandBuilderHelper.getHelper(connection).getColumnInfos(fqn.getSchema("%"), fqn.getName());

        String statement = "select * from " + tableName + " where ";
        boolean primaryFound = false;

        for (ColumnInfo info : infos) {
            if (info.isPrimary()) {
                statement += CommandBuilderHelper.getHelper(connection).getColumnName(info.getColumnName()) + "=?";
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

    public static Command build(Connection connection, String tableName, Object obj) throws SQLException {
        FQN fqn = new FQN(connection, tableName);
        final CommandBuilderHelper helper = CommandBuilderHelper.getHelper(connection);
        ColumnInfo[] infos = helper.getColumnInfos(fqn.getSchema("%"), fqn.getName());

        List values = new ArrayList();
        String statement = "select * from " + tableName + " where ";
        for (ColumnInfo info : infos) {
            if (info.isPrimary()) {
                statement += helper.getColumnName(info.getColumnName()) + "=?";
                values.add(BeanUtil.getValue(obj, TypeConverter.getFieldName(info.getColumnName())));
                break;
            }
        }

        return new Command(statement, values);
    }
}

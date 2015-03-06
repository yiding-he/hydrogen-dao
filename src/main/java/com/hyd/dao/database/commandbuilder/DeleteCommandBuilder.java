package com.hyd.dao.database.commandbuilder;

import com.hyd.dao.database.ColumnInfo;
import com.hyd.dao.database.commandbuilder.helper.CommandBuilderHelper;

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

    public static Command build(Connection conn, String tableName, Object object) throws SQLException {
        FQN fqn = new FQN(conn, tableName);
        ColumnInfo[] infos = CommandBuilderHelper.getHelper(conn).getColumnInfos(fqn.getSchema("%"), fqn.getName());
        return buildCommand(tableName, infos, object, conn);
    }

    private static Command buildCommand(
            String tableName, ColumnInfo[] infos, Object object, Connection conn) throws SQLException {

        final CommandBuilderHelper helper = CommandBuilderHelper.getHelper(conn);
        String command = "delete from " + tableName;
        String whereMarks = "";
        List<Object> whereParams = new ArrayList<Object>();
        for (ColumnInfo info : infos) {
            if (info.isPrimary()) {
                whereMarks += helper.getColumnName(info.getColumnName()) + "=? and";
                whereParams.add(CommandBuilderHelper.generateParamValue(object, info));
            }
        }
        if ("".equals(whereMarks)) {
            throw new NoPrimaryKeyException("no primary key found in table \"" + tableName + "\"");
        }

        if (whereMarks.endsWith("and")) {
            whereMarks = whereMarks.substring(0, whereMarks.length() - 3);
        }

        command += " where " + whereMarks;

        List<Object> params = new ArrayList<Object>();
        params.addAll(whereParams);
        return new Command(command, params);

    }

    public static Command buildByKey(Connection connection, String tableName, Object key) throws SQLException {
        FQN fqn = new FQN(connection, tableName);
        final CommandBuilderHelper helper = CommandBuilderHelper.getHelper(connection);

        ColumnInfo[] infos = helper.getColumnInfos(fqn.getSchema("%"), fqn.getName());

        String statement = "delete from " + tableName + " where ";
        for (ColumnInfo info : infos) {
            if (info.isPrimary()) {
                statement += helper.getColumnName(info.getColumnName()) + "=?";
                break;
            }
        }

        List<Object> params = new ArrayList<Object>();
        params.add(key);
        return new Command(statement, params);
    }
}

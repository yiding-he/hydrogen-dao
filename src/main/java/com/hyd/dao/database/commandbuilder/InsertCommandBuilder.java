package com.hyd.dao.database.commandbuilder;

import com.hyd.dao.BatchCommand;
import com.hyd.dao.DAO;
import com.hyd.dao.DAOException;
import com.hyd.dao.database.ColumnInfo;
import com.hyd.dao.database.commandbuilder.helper.CommandBuilderHelper;
import com.hyd.dao.database.connection.ConnectionUtil;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 创建 insert 语句
 */
public class InsertCommandBuilder {

    private InsertCommandBuilder() {

    }

    /**
     * 构造一个批处理命令。注意：批处理命令 SQL 语句中有哪些参数，是根据第一个要插入的记录生成的。
     * 这时候不能因为记录的某个属性值为 null 就不将该属性加入 SQL，因为后面的其他记录的属性值可能不是 null。
     *
     * @param conn      数据库连接
     * @param tableName 表名
     * @param objects   要插入的记录对象
     *
     * @return 批处理插入命令
     *
     * @throws java.sql.SQLException 如果获取数据库信息失败
     */
    public static BatchCommand buildBatch(Connection conn, String tableName, List objects) throws SQLException {

        if (objects == null || objects.isEmpty()) {
            return BatchCommand.EMPTY;
        }

        final CommandBuilderHelper helper = CommandBuilderHelper.getHelper(conn);
        ColumnInfo[] infos = getBatchColumnInfo(conn, tableName, helper, objects.get(0));

        String statement = "insert into " + helper.getTableNameForSql(tableName) + "(";
        String values = "";
        for (ColumnInfo info : infos) {
            String columnName;

            boolean isUsingSysdate = info.getDataType() == DAO.SYSDATE_TYPE;
            if (isUsingSysdate) {
                columnName = helper.getSysdateMark();
            } else {
                columnName = helper.getColumnName(info.getColumnName());
            }

            statement += columnName + ",";
            values += "?,";
        }

        statement = StringUtils.removeEnd(statement, ",")
                + ") values ("
                + StringUtils.removeEnd(values, ",") + ")";

        // 生成命令
        BatchCommand bc = new BatchCommand(statement);
        bc.setColumnInfos(infos);

        for (Object object : objects) {
            bc.addParams(CommandBuilderHelper.generateParams(infos, object));
        }
        return bc;
    }

    // 获取要批量插入的表字段信息
    private static ColumnInfo[] getBatchColumnInfo(Connection conn, String tableName,
                                                   CommandBuilderHelper helper, Object sample) throws SQLException {

        FQN fqn = new FQN(conn, tableName);
        ColumnInfo[] infos = helper.getColumnInfos(fqn.getSchema("%"), fqn.getName());
        List list = CommandBuilderHelper.generateParams(infos, sample);

        for (int i = 0, listSize = list.size(); i < listSize; i++) {
            Object propertyValue = list.get(i);
            if (propertyValue == DAO.SYSDATE) {
                infos[i].setDataType(DAO.SYSDATE_TYPE);
            }
        }
        return infos;
    }

    /**
     * 构造一条插入命令
     *
     * @param connection 数据库连接
     * @param tableName  表名
     * @param object     要插入的对象
     *
     * @return 插入命令
     *
     * @throws SQLException 如果获取数据库信息失败
     */
    public static Command build(Connection connection, String tableName, Object object) throws SQLException {
        FQN fqn = new FQN(connection, tableName);

        CommandBuilderHelper helper = CommandBuilderHelper.getHelper(connection);

        ColumnInfo[] infos = helper.getColumnInfos(fqn.getSchema(), fqn.getName());
        return buildCommand(tableName, infos, object, connection);
    }

    /**
     * 构造一条插入命令
     *
     * @param tableName 表名
     * @param infos     表的字段信息
     * @param object    要插入的对象
     * @param conn      数据库连接
     *
     * @return 插入命令
     *
     * @throws java.sql.SQLException 如果获取数据库类型失败
     */
    private static Command buildCommand(String tableName, ColumnInfo[] infos, Object object, Connection conn)
            throws SQLException {
        List params = CommandBuilderHelper.generateParams(infos, object);
        List<Object> finalParams = new ArrayList<Object>();

        final CommandBuilderHelper helper = CommandBuilderHelper.getHelper(conn);

        String command = "insert into " + tableName + "(";
        String questionMarks = "";

        for (int i = 0; i < infos.length; i++) {
            Object value = params.get(i);

            if (value == null) {
                continue;
            }

            String columnName = helper.getColumnName(infos[i].getColumnName());
            command += columnName + ",";

            // 属性值是一个 sysdate 占位符
            if (value == DAO.SYSDATE) {
                questionMarks += helper.getSysdateMark() + ",";
                continue;
            }

            // 如果属性值是一个 sequence 占位符，那么生成相应的 SQL，而 value 就不必作为参数了。
            if (infos[i].isAutoIncrement()) {
                if (ConnectionUtil.isSequenceSupported(conn) && infos[i].getSequenceName() == null) {
                    throw new DAOException("没有指定全局序列");
                }

                if (infos[i].getSequenceName() != null && ConnectionUtil.isSequenceSupported(conn)) {
                    questionMarks += infos[i].getSequenceName() + ".nextval,";
                }

                // 如果没有指定 sequenceName，说明数据库会自动加1（例如 MySQL），所以语句中可以忽略该字段
                continue;
            }

            finalParams.add(value);

            questionMarks += "?" + ",";
        }

        command = StringUtils.removeEnd(command, ",");
        questionMarks = StringUtils.removeEnd(questionMarks, ",");

        command += ") values (" + questionMarks + ")";

        List<Integer> paramTypes = new ArrayList<Integer>();
        for (ColumnInfo info : infos) {
            paramTypes.add(info.getDataType());
        }
        return new Command(command, finalParams, paramTypes);
    }

}

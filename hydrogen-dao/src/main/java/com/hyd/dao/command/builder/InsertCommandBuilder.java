package com.hyd.dao.command.builder;

import com.hyd.dao.DAO;
import com.hyd.dao.DAOException;
import com.hyd.dao.command.BatchCommand;
import com.hyd.dao.command.Command;
import com.hyd.dao.command.builder.helper.CommandBuilderHelper;
import com.hyd.dao.database.ColumnInfo;
import com.hyd.dao.database.DatabaseType;
import com.hyd.dao.database.FQN;
import com.hyd.dao.mate.util.ConnectionContext;
import com.hyd.dao.mate.util.Str;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 创建 insert 语句
 */
public final class InsertCommandBuilder {

    private final ConnectionContext context;

    public InsertCommandBuilder(ConnectionContext context) {
        this.context = context;
    }

    /**
     * 构造一个批处理命令。注意：批处理命令 SQL 语句中有哪些参数，是根据第一个要插入的记录生成的。
     * 这时候不能因为记录的某个属性值为 null 就不将该属性加入 SQL，因为后面的其他记录的属性值可能不是 null。
     *
     * @param tableName 表名
     * @param objects   要插入的记录对象
     *
     * @return 批处理插入命令
     */
    public BatchCommand buildBatch(String tableName, List objects) {

        if (objects == null || objects.isEmpty()) {
            return BatchCommand.EMPTY;
        }

        final CommandBuilderHelper helper = CommandBuilderHelper.getHelper(context);
        ColumnInfo[] infos = getBatchColumnInfo(context, tableName, helper, objects.get(0));

        StringBuilder statement = new StringBuilder("insert into " + helper.getTableNameForSql(tableName) + "(");
        StringBuilder values = new StringBuilder();
        for (ColumnInfo info : infos) {
            String columnName;

            boolean isUsingSysdate = info.getDataType() == DAO.SYSDATE_TYPE;
            if (isUsingSysdate) {
                columnName = helper.getSysdateMark();
            } else {
                columnName = helper.getStrictName(info.getColumnName());
            }

            statement.append(columnName).append(",");
            values.append("?,");
        }

        statement = new StringBuilder(Str.removeEnd(statement.toString(), ",")
            + ") values ("
            + Str.removeEnd(values.toString(), ",") + ")");

        // 生成命令
        BatchCommand bc = new BatchCommand(statement.toString());
        bc.setColumnInfos(infos);

        for (Object object : objects) {
            bc.addParams(helper.generateParams(infos, object));
        }
        return bc;
    }

    // 获取要批量插入的表字段信息
    private static ColumnInfo[] getBatchColumnInfo(
        ConnectionContext context, String tableName, CommandBuilderHelper helper, Object sample
    ) {

        FQN fqn = new FQN(context, tableName);
        ColumnInfo[] infos = helper.getColumnInfos(fqn.getSchema("%"), fqn.getName());
        List list = helper.generateParams(infos, sample);

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
     * @param tableName 表名
     * @param object    要插入的对象
     *
     * @return 插入命令
     * @throws SQLException 如果获取数据库信息失败
     */
    public Command build(String tableName, Object object) throws DAOException {
        FQN fqn = new FQN(context, tableName);
        CommandBuilderHelper helper = CommandBuilderHelper.getHelper(context);
        ColumnInfo[] infos = helper.getColumnInfos(fqn.getSchema(), fqn.getName());
        List params = helper.generateParams(infos, object);
        return buildCommand(tableName, infos, params, context);
    }

    /**
     * 构造一条插入命令
     *
     * @param tableName 表名
     * @param infos     表的字段信息
     * @param params    参数值
     * @param context   数据库操作上下文
     *
     * @return 插入命令
     */
    private Command buildCommand(
        String tableName, ColumnInfo[] infos, List params, ConnectionContext context
    ) {

        DatabaseType databaseType = DatabaseType.of(context.getConnection());
        List<Object> finalParams = new ArrayList<>();

        final CommandBuilderHelper helper = CommandBuilderHelper.getHelper(context);

        StringBuilder command = new StringBuilder("insert into " + tableName + "(");
        StringBuilder questionMarks = new StringBuilder();

        for (int i = 0; i < infos.length; i++) {
            Object value = params.get(i);

            if (value == null) {
                continue;
            }

            String columnName = helper.getStrictName(infos[i].getColumnName());
            command.append(columnName).append(",");

            // 属性值是一个 sysdate 占位符
            if (value == DAO.SYSDATE) {
                questionMarks.append(helper.getSysdateMark()).append(",");
                continue;
            }

            // 如果属性值是一个 sequence 占位符，那么生成相应的 SQL，而 value 就不必作为参数了。
            if (infos[i].isAutoIncrement()) {
                if (databaseType.isSequenceSupported() && infos[i].getSequenceName() == null) {
                    throw new DAOException("没有指定全局序列");
                }

                if (infos[i].getSequenceName() != null && databaseType.isSequenceSupported()) {
                    questionMarks.append(infos[i].getSequenceName()).append(".nextval,");
                }

                // 如果没有指定 sequenceName，说明数据库会自动加1（例如 MySQL），所以语句中可以忽略该字段
                continue;
            }

            finalParams.add(value);

            questionMarks.append("?" + ",");
        }

        command = new StringBuilder(Str.removeEnd(command.toString(), ","));
        questionMarks = new StringBuilder(Str.removeEnd(questionMarks.toString(), ","));

        command.append(") values (").append(questionMarks).append(")");

        List<Integer> paramTypes = new ArrayList<>();
        for (ColumnInfo info : infos) {
            paramTypes.add(info.getDataType());
        }
        return new Command(command.toString(), finalParams);
    }

}

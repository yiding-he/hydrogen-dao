package com.hyd.dao.command.builder;

import com.hyd.dao.DAO;
import com.hyd.dao.DAOException;
import com.hyd.dao.SQL;
import com.hyd.dao.command.BatchCommand;
import com.hyd.dao.command.Command;
import com.hyd.dao.command.builder.helper.CommandBuilderHelper;
import com.hyd.dao.database.ColumnInfo;
import com.hyd.dao.database.FQN;
import com.hyd.dao.mate.util.ConnectionContext;

import java.util.List;

/**
 * 创建 insert 语句
 */
public final class InsertBuilder extends CommandBuilder {

    public InsertBuilder(ConnectionContext context) {
        super(context);
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
    public BatchCommand buildBatch(String tableName, List<?> objects) {

        if (objects == null || objects.isEmpty()) {
            return BatchCommand.EMPTY;
        }

        final CommandBuilderHelper helper = CommandBuilderHelper.getHelper(context);
        final FQN fqn = new FQN(context, tableName);
        final Object sample = objects.get(0);
        final ColumnInfo[] infos = getBatchColumnInfo(context, tableName, helper, sample);
        final SQL.Insert insert = new SQL.Insert(fqn.getStrictName());

        for (ColumnInfo info : infos) {
            boolean isUsingSysdate = info.getDataType() == DAO.SYSDATE_TYPE;
            String columnName;

            if (isUsingSysdate) {
                columnName = helper.getSysdateMark();
            } else {
                columnName = helper.getStrictName(info.getColumnName());
            }

            insert.Values(columnName, new Object());
        }

        // 生成命令
        BatchCommand bc = new BatchCommand(insert.toCommand().getStatement());
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
        ColumnInfo[] originColInfos = helper.getColumnInfos(fqn);
        ColumnInfo[] infos = helper.filterColumnsByType(originColInfos, sample.getClass());

        List<Object> list = helper.generateParams(infos, sample);
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
     *
     * @throws DAOException 如果获取数据库信息失败
     */
    public Command build(String tableName, Object object) throws DAOException {
        FQN fqn = new FQN(context, tableName);
        CommandBuilderHelper helper = CommandBuilderHelper.getHelper(context);
        ColumnInfo[] infos = helper.filterColumnsByType(helper.getColumnInfos(fqn), object.getClass());
        List<Object> params = helper.generateParams(infos, object);
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
        String tableName, ColumnInfo[] infos, List<Object> params, ConnectionContext context
    ) {
        final FQN fqn = new FQN(context, tableName);
        final CommandBuilderHelper helper = CommandBuilderHelper.getHelper(context);
        final SQL.Insert insert = new SQL.Insert(fqn.getStrictName());

        for (int i = 0; i < infos.length; i++) {
            Object value = params.get(i);
            if (value == null) {
                continue;
            }

            String columnName = helper.getStrictName(infos[i].getColumnName());
            insert.Values(columnName, value);
        }

        return insert.toCommand();
    }

}

package com.hyd.dao.command.builder;

import com.hyd.dao.DAO;
import com.hyd.dao.DAOException;
import com.hyd.dao.SQL;
import com.hyd.dao.command.BatchCommand;
import com.hyd.dao.command.Command;
import com.hyd.dao.database.ColumnInfo;
import com.hyd.dao.database.ConnectionContext;
import com.hyd.dao.database.FQN;
import com.hyd.dao.database.type.NameConverter;

import java.util.Collection;
import java.util.List;

import static com.hyd.dao.command.builder.helper.CommandBuilderHelper.*;

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
    public BatchCommand buildBatch(String tableName, Collection<?> objects) {

        if (objects == null || objects.isEmpty()) {
            return BatchCommand.EMPTY;
        }

        final NameConverter nameConverter = context.getNameConverter();
        final FQN fqn = new FQN(context, tableName);
        final Object sample = objects.iterator().next();
        final List<ColumnInfo> infos = getBatchColumnInfo(context, tableName, sample);
        final SQL.Insert insert = new SQL.Insert(fqn.getQuotedName());

        for (ColumnInfo info : infos) {
            boolean isUsingSysdate = info.getDataType() == DAO.SYSDATE_TYPE;
            String columnName;

            if (isUsingSysdate) {
                columnName = context.getDialect().currentTimeExpression();
            } else {
                columnName = context.getDialect().quote(info.getColumnName());
            }

            insert.Values(columnName, new Object());
        }

        // 生成命令
        BatchCommand bc = new BatchCommand(insert.toCommand().getStatement());
        bc.setColumnInfos(infos);

        for (Object object : objects) {
            bc.addParams(generateParams(infos, object, nameConverter));
        }
        return bc;
    }

    // 获取要批量插入的表字段信息
    private static List<ColumnInfo> getBatchColumnInfo(
        ConnectionContext context, String tableName, Object sample
    ) {

        NameConverter nameConverter = context.getNameConverter();
        FQN fqn = new FQN(context, tableName);
        List<ColumnInfo> originColInfos = getColumnInfos(fqn, context);
        List<ColumnInfo> infos = filterColumnsByType(originColInfos, sample.getClass(), nameConverter);

        List<Object> list = generateParams(infos, sample, nameConverter);
        for (int i = 0, listSize = list.size(); i < listSize; i++) {
            Object propertyValue = list.get(i);
            if (propertyValue == DAO.SYSDATE) {
                infos.get(i).setDataType(DAO.SYSDATE_TYPE);
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
        NameConverter nameConverter = context.getNameConverter();
        List<ColumnInfo> infos = filterColumnsByType(getColumnInfos(fqn, context), object.getClass(), nameConverter);
        List<Object> params = generateParams(infos, object, nameConverter);
        return buildCommand(tableName, infos, params);
    }

    /**
     * 构造一条插入命令
     *
     * @param tableName 表名
     * @param infos     表的字段信息
     * @param params    参数值
     *
     * @return 插入命令
     */
    private Command buildCommand(String tableName, List<ColumnInfo> infos, List<Object> params) {
        final FQN fqn = new FQN(context, tableName);
        final SQL.Insert insert = new SQL.Insert(fqn.getQuotedName());

        for (int i = 0; i < infos.size(); i++) {
            Object value = params.get(i);
            if (value == null) {
                continue;
            }

            String columnName = context.getDialect().quote(infos.get(i).getColumnName());
            insert.Values(columnName, value);
        }

        return insert.toCommand();
    }

}

package com.hyd.dao.command.builder;

import com.hyd.dao.SQL;
import com.hyd.dao.command.Command;
import com.hyd.dao.command.builder.helper.CommandBuilderHelper;
import com.hyd.dao.database.ColumnInfo;
import com.hyd.dao.database.ConnectionContext;
import com.hyd.dao.database.FQN;
import com.hyd.dao.database.type.NameConverter;

import java.util.List;

public class UpdateBuilder extends CommandBuilder {

    public UpdateBuilder(ConnectionContext context) {
        super(context);
    }

    /**
     * 生成根据主键值更新字段 Update 命令
     *
     * @param tableName 表名
     * @param object    包含主键值和要更新的字段值的对象
     *
     * @return 生成的命令
     */
    public Command buildByKey(String tableName, Object object) {
        if (object == null) {
            throw new NullPointerException("object is null");
        }

        final NameConverter nameConverter = context.getNameConverter();
        final FQN fqn = new FQN(context, tableName);
        final List<ColumnInfo> infos = CommandBuilderHelper.getColumnInfos(fqn, context);
        final SQL.Update update = new SQL.Update(tableName);

        for (ColumnInfo info : infos) {
            String columnName = context.getDialect().quote(info.getColumnName());
            Object param = CommandBuilderHelper.generateParamValue(object, info, nameConverter);

            if (info.isPrimary()) {
                if (param == null) {
                    throw new IllegalStateException("Update command missing param value for column " + columnName);
                }
                update.Where(columnName + "=?", param);
            } else {
                update.Set(param != null, columnName, param);
            }
        }

        if (!update.hasConditions()) {
            throw new IllegalStateException(
                "Update command has no condition, dangerous operation prohibited.");
        }

        return update.toCommand();
    }
}

package com.hyd.dao.command.builder;

import com.hyd.dao.SQL;
import com.hyd.dao.command.Command;
import com.hyd.dao.command.builder.helper.CommandBuilderHelper;
import com.hyd.dao.database.ColumnInfo;
import com.hyd.dao.database.FQN;
import com.hyd.dao.mate.util.ConnectionContext;

public class UpdateBuilder extends CommandBuilder {

    public UpdateBuilder(ConnectionContext context) {
        super(context);
    }

    public Command buildByKey(String tableName, Object object) {
        if (object == null) {
            throw new NullPointerException("object is null");
        }

        final FQN fqn = new FQN(context, tableName);
        final CommandBuilderHelper helper = CommandBuilderHelper.getHelper(context);
        final ColumnInfo[] infos = helper.getColumnInfos(fqn.getSchema("%"), fqn.getName());
        SQL.Update update = new SQL.Update(tableName);

        for (ColumnInfo info : infos) {
            String columnName = helper.getStrictName(info.getColumnName());
            Object param = helper.generateParamValue(object, info);

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

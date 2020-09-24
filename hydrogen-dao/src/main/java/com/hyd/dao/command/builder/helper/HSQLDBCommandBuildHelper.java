package com.hyd.dao.command.builder.helper;

import com.hyd.dao.database.executor.ExecutionContext;

/**
 * (description)
 * created at 2015/2/10
 *
 * @author Yiding
 */
public class HSQLDBCommandBuildHelper extends CommandBuilderHelper {

    public HSQLDBCommandBuildHelper(ExecutionContext context) {
        super(context);
    }

    @Override
    protected String getSchema(String schema) {
        return "PUBLIC";
    }

    @Override
    protected String getTableNameForMeta(String tableName) {
        return tableName.toUpperCase();
    }

    @Override
    protected String getCatalog() {
        return null;
    }

    @Override
    public String getSysdateMark() {
        return "CURRENT_DATE";
    }

    @Override
    public String getRangedSql(String sql, int startPos, int endPos) {
        int size = endPos - startPos;
        return "select range_wrapper.* from (" + sql + ") range_wrapper " +
            " limit " + startPos + "," + size;
    }
}

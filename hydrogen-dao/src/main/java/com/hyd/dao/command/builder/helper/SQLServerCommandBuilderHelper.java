package com.hyd.dao.command.builder.helper;

import com.hyd.dao.database.executor.ExecutionContext;

/**
 * (description)
 * created at 2015/5/16
 *
 * @author Yiding
 */
public class SQLServerCommandBuilderHelper extends CommandBuilderHelper {

    public SQLServerCommandBuilderHelper(ExecutionContext context) {
        super(context);
    }

    @Override
    protected String getSchema(String schema) {
        return "%";
    }
}

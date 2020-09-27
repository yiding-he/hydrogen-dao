package com.hyd.dao.command.builder.helper;

import com.hyd.dao.mate.util.ConnectionContext;

/**
 * (description)
 * created at 2015/5/16
 *
 * @author Yiding
 */
public class SQLServerCommandBuilderHelper extends CommandBuilderHelper {

    public SQLServerCommandBuilderHelper(ConnectionContext context) {
        super(context);
    }

    @Override
    protected String getSchema(String schema) {
        return "%";
    }
}

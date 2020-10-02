package com.hyd.dao.command.builder.helper;

/**
 * (description)
 * created at 2015/5/16
 *
 * @author Yiding
 */
public class SQLServerSqlFix implements SqlFix {

    @Override
    public String getSchema(String schema) {
        return "%";
    }
}

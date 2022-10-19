package com.hyd.dao.database.dialects.impl;

import com.hyd.dao.DAOException;
import com.hyd.dao.database.dialects.Dialect;
import com.hyd.dao.database.type.NameConverter;
import com.hyd.dao.mate.util.ResultSetUtil;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class OracleDialect implements Dialect {

    private static final Pattern PRODUCT_NAME_PATTERN = Pattern.compile(".*Oracle.*");

    public static final int TYPE_CURSOR = -10;

    @Override
    public String identityQuoter() {
        return "\"";
    }

    @Override
    public Predicate<Connection> getMatcher() {
        return connection -> {
            try {
                var databaseProductName = connection.getMetaData().getDatabaseProductName();
                return PRODUCT_NAME_PATTERN.matcher(databaseProductName).matches();
            } catch (SQLException e) {
                throw new DAOException(e);
            }
        };
    }

    @Override
    public String wrapRangeQuery(String sql, int startPos, int endPos) {
        var _startPos = startPos + 1;
        var sql_prefix = "select * from ( select pagination_wrapper.*, rownum " +
            ResultSetUtil.PAGINATION_WRAPPER_COLUMN_NAME + " from (";
        var sql_suffix = ") pagination_wrapper) where " +
            ResultSetUtil.PAGINATION_WRAPPER_COLUMN_NAME + " between " + _startPos + " and " + endPos;
        return sql_prefix + sql + sql_suffix;
    }

    @Override
    public Object parseCallableStatementResult(int sqlType, Object value) {
        try {
            if (sqlType == TYPE_CURSOR) {
                var rs1 = (ResultSet) value;
                return ResultSetUtil.readResultSet(rs1, null, NameConverter.DEFAULT, -1, -1);
            } else {
                return Dialect.super.parseCallableStatementResult(sqlType, value);
            }
        } catch (Exception e) {
            throw DAOException.wrap(e);
        }
    }
}

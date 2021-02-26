package com.hyd.dao.database.dialects.impl;

import com.hyd.dao.DAOException;
import com.hyd.dao.database.dialects.Dialect;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class MsSqlServerDialect implements Dialect {

    private static final Pattern PRODUCT_NAME_PATTERN = Pattern.compile(".*Microsoft SQL Server.*");

    @Override
    public Predicate<Connection> getMatcher() {
        return c -> {
            try {
                return PRODUCT_NAME_PATTERN.matcher(c.getMetaData().getDatabaseProductName()).matches();
            } catch (SQLException e) {
                throw new DAOException(e);
            }
        };
    }

    @Override
    public String wrapRangeQuery(String sql, int startPos, int endPos) {
        return sql + " offset " + startPos + " rows fetch next " + (endPos - startPos) + " rows only";
    }

    @Override
    public int resultSetTypeForReading() {
        return ResultSet.TYPE_SCROLL_SENSITIVE;
    }
}

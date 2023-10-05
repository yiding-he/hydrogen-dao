package com.hyd.dao.database.dialects.impl;

import com.hyd.dao.DAOException;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * TiDB 7.3.0 存在 BUG 会令分页查询失去排序，所以针对这个版本的分页必须使用单独的方式
 */
public class TidbDialect extends MySqlDialect {

    private static final Pattern PRODUCT_NAME_PATTERN = Pattern.compile(".*MySQL.*");
    private static final Pattern PRODUCT_VERSION_PATTERN = Pattern.compile(".*TiDB.*-v7\\.3\\..*");

    @Override
    public Predicate<Connection> getMatcher() {
        return c -> {
            try {
                var databaseProductName = c.getMetaData().getDatabaseProductName();
                var databaseProductVersion = c.getMetaData().getDatabaseProductVersion();
                return
                    PRODUCT_NAME_PATTERN.matcher(databaseProductName).matches() &&
                    PRODUCT_VERSION_PATTERN.matcher(databaseProductVersion).matches();
            } catch (SQLException e) {
                throw new DAOException(e);
            }
        };
    }

    @Override
    public String wrapRangeQuery(String sql, int startPos, int endPos) {
        int size = endPos - startPos;
        return sql + " limit " + startPos + ", " + size;
    }
}

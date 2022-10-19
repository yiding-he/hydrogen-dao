package com.hyd.dao.database.dialects.impl;

import com.hyd.dao.DAOException;
import com.hyd.dao.database.ColumnInfo;
import com.hyd.dao.database.FQN;
import com.hyd.dao.database.dialects.Dialect;
import com.hyd.dao.database.executor.ExecuteMode;
import com.hyd.dao.mate.util.Str;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class MySqlDialect implements Dialect {

    private static final Pattern PRODUCT_NAME_PATTERN = Pattern.compile(".*MySQL.*");

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
        int size = endPos - startPos;
        return "select range_wrapper.* from (" + sql + ") range_wrapper limit " + startPos + ", " + size;
    }

    @Override
    public String wrapCountQuery(String sql) {
        return "select count(*) cnt from (" + sql + ") count_sql_wrapper";
    }

    @Override
    public String identityQuoter() {
        return "`";
    }

    @Override
    public String getJavaTypeByDatabase(ColumnInfo columnInfo) {
        if (columnInfo.getDataType() == Types.DECIMAL) {
            return "Double";
        } else {
            return "String";
        }
    }

    // MySQL 遇到 catalog 为空时会强行取 Connection 的 catalog 而忽略 schema，
    // 所以这里当 FQN 指定了 schema 时，要用它来取代 catalog
    @Override
    public String fixCatalog(String connectionCatalog, FQN fqn) {
        return Str.defaultIfEmpty(fqn.getSchema(), connectionCatalog);
    }

    @Override
    public void setupStatement(Statement statement, ExecuteMode executeMode) throws SQLException {
        if (executeMode == ExecuteMode.Streaming) {
            // https://dev.mysql.com/doc/connector-j/8.0/en/connector-j-reference-implementation-notes.html
            // Chapter "ResultSet":
            // "If you are working with ResultSets that have a large number of rows or large values
            //  and cannot allocate heap space in your JVM for the memory required, you can tell the driver
            //  to stream the results back one row at a time."
            statement.setFetchSize(Integer.MIN_VALUE);
        }
    }
}

package com.hyd.dao.database.dialects;

import java.sql.Connection;
import java.util.function.Predicate;

public class DefaultDialect implements Dialect {

    public static final DefaultDialect VALUE = new DefaultDialect();

    @Override
    public Predicate<Connection> getMatcher() {
        return c -> true;
    }

    @Override
    public String wrapRangeQuery(String sql, int startPos, int endPos) {
        throw new UnsupportedOperationException();
    }
}

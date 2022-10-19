package com.hyd.dao.database.dialects;

import com.hyd.dao.database.dialects.impl.H2Dialect;
import com.hyd.dao.database.dialects.impl.MsSqlServerDialect;
import com.hyd.dao.database.dialects.impl.MySqlDialect;
import com.hyd.dao.database.dialects.impl.OracleDialect;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class Dialects {

    /**
     * 注册所有 Dialect，优先级从低到高
     */
    private static final List<Dialect> DIALECTS = new ArrayList<>();

    static {
        DIALECTS.add(new DefaultDialect());
        DIALECTS.add(new OracleDialect());
        DIALECTS.add(new MsSqlServerDialect());
        DIALECTS.add(new MySqlDialect());
        DIALECTS.add(new H2Dialect());
    }

    public static void registerDialect(Dialect dialect) {
        DIALECTS.add(dialect);
    }

    public static Dialect getDialect(Connection connection) {
        for (var i = DIALECTS.size() - 1; i >= 0; i--) {
            var dialect = DIALECTS.get(i);
            if (dialect.getMatcher().test(connection)) {
                return dialect;
            }
        }
        return new DefaultDialect();
    }
}

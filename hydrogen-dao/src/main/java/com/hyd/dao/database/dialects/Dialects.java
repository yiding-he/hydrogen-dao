package com.hyd.dao.database.dialects;

import com.hyd.dao.database.dialects.impl.*;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class Dialects {

    /**
     * 注册所有 Dialect，优先级从低到高（遍历是逆序的）
     */
    private static final List<Dialect> DIALECTS = new ArrayList<>();

    static {
        DIALECTS.add(new DefaultDialect());
        DIALECTS.add(new OracleDialect());
        DIALECTS.add(new MsSqlServerDialect());
        DIALECTS.add(new MySqlDialect());
        DIALECTS.add(new H2Dialect());
        DIALECTS.add(new TidbDialect());
    }

    public static void registerDialect(Dialect dialect) {
        DIALECTS.add(dialect);
    }

    public static Dialect getDialect(Connection connection) {
        for (int i = DIALECTS.size() - 1; i >= 0; i--) {
            Dialect dialect = DIALECTS.get(i);
            if (dialect.getMatcher().test(connection)) {
                return dialect;
            }
        }
        return new DefaultDialect();
    }
}

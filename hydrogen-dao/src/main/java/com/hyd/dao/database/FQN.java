package com.hyd.dao.database;

import com.hyd.dao.DAOException;
import com.hyd.dao.mate.util.ConnectionContext;
import com.hyd.dao.mate.util.Str;

import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * FQN of a table.
 *
 * @author yiding.he
 */
public class FQN {

    private static final Map<String, String> SCHEMA_CACHE = new ConcurrentHashMap<>();

    private final String schema;

    private final String name;

    public FQN(ConnectionContext context, String fqn) {
        if (Str.isEmpty(fqn)) {
            throw new IllegalArgumentException("FQN parameter cannot be empty");
        }

        String dataSourceName = context.getDataSourceName();
        this.schema = SCHEMA_CACHE.computeIfAbsent(dataSourceName, _ds -> {
            try {
                if (fqn.contains(".")) {
                    return Str.subStringBeforeLast(fqn, ".");
                } else {
                    return context.getConnection().getSchema();
                }
            } catch (SQLException e) {
                throw new DAOException(e);
            }
        });

        this.name = fqn.contains(".") ? Str.subStringAfterLast(fqn, ".") : fqn;
    }

    public String getSchema(String defaultValue) {
        return Str.defaultIfEmpty(schema, defaultValue);
    }

    public String getName(String defaultValue) {
        return Str.defaultIfEmpty(name, defaultValue);
    }

    public String getSchema() {
        return schema;
    }

    public String getName() {
        return name;
    }
}

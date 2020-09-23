package com.hyd.dao.database.commandbuilder;

import com.hyd.dao.DAOException;
import com.hyd.dao.database.executor.ExecutionContext;
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

    private String schema;

    private String name;

    public FQN(ExecutionContext context, String fqn) {
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

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getName(String defaultValue) {
        return Str.defaultIfEmpty(name, defaultValue);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSchema() {
        return schema;
    }

    public String getName() {
        return name;
    }
}

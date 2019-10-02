package com.hyd.dao.database.commandbuilder;

import com.hyd.dao.util.Str;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * FQN of a table.
 *
 * @author yiding.he
 */
public class FQN {

    private String schema;

    private String name;

    public FQN(Connection conn, String fqn) throws SQLException {
        if (Str.isEmpty(fqn)) {
            throw new IllegalArgumentException("FQN parameter cannot be empty");
        }

        if (!fqn.contains(".")) {
            schema = conn.getMetaData().getUserName();
            name = fqn;
        } else {
            String[] splitted = fqn.split("[.]");
            schema = splitted[splitted.length - 2];
            name = splitted[splitted.length - 1];
        }
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

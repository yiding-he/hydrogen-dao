package com.hyd.dao.database.commandbuilder;

import org.apache.commons.lang.StringUtils;

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
        if (StringUtils.isEmpty(fqn)) {
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
        return StringUtils.defaultIfEmpty(schema, defaultValue);
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getName(String defaultValue) {
        return StringUtils.defaultIfEmpty(name, defaultValue);
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

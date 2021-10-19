package com.hyd.dao.database;

import com.hyd.dao.DAOException;
import com.hyd.dao.mate.util.Str;
import lombok.EqualsAndHashCode;

import java.sql.SQLException;

/**
 * FQN of a table.
 *
 * @author yiding.he
 */
@EqualsAndHashCode
public class FQN {

    private final String schema;

    private final String name;

    private final String quotedName;

    public FQN(ConnectionContext context, String fqn) {
        if (Str.isEmpty(fqn)) {
            throw new IllegalArgumentException("FQN parameter cannot be empty");
        }

        this.schema = parseSchema(context, fqn);
        this.name = fqn.contains(".") ? Str.subStringAfterLast(fqn, ".") : fqn;
        this.quotedName = context.getDialect().quote(this.schema, this.name);
    }

    private String parseSchema(ConnectionContext context, String fqn) {
        try {
            // 如果表名带 "." 则取前面部分，否则从 Connection 对象中取，如果取不到则使用 "%"
            if (fqn.contains(".")) {
                return Str.subStringBeforeLast(fqn, ".");
            } else {
                return Str.fromCandidates(
                    context.getDriverConnection().getSchema(),
                    context.getDriverConnection().getCatalog(),
                    "%"
                );
            }
        } catch (SQLException e) {
            throw new DAOException(e);
        }
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

    public String getQuotedName() {
        return quotedName;
    }

    public String getFullName() {
        return Str.appendIfNotEmpty(schema, ".", "") + name;
    }
}

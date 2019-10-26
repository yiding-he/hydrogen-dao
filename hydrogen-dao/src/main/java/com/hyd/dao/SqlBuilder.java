package com.hyd.dao;

import com.hyd.dao.database.commandbuilder.Command;

import java.util.Collections;

public abstract class SqlBuilder {

    private StringBuilder sb = new StringBuilder();

    private void append(String string) {
        sb.append(string);
    }

    ////////////////////////////////////////////////////////////

    protected class SelectContext {

        private SelectContext(String... columns) {
            append("select " + String.join(",", columns));
        }

        protected FromContext from(String... tables) {
            return new FromContext(tables);
        }
    }

    protected class FromContext {

        private FromContext(String... tables) {
            append(" from " + String.join(",", tables));
        }

        protected SqlBuilder union() {
            append(" union ");
            return SqlBuilder.this;
        }

        protected SqlBuilder unionAll() {
            append(" union all ");
            return SqlBuilder.this;
        }
    }

    ////////////////////////////////////////////////////////////

    protected SelectContext select(String... columns) {
        return new SelectContext(columns);
    }

    public Command toCommand() {
        return new Command(sb.toString(), Collections.emptyList());
    }
}

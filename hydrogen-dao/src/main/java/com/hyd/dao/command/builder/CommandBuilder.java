package com.hyd.dao.command.builder;

import com.hyd.dao.database.ConnectionContext;

public abstract class CommandBuilder {

    protected final ConnectionContext context;

    public CommandBuilder(ConnectionContext context) {
        this.context = context;
    }
}

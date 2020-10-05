package com.hyd.dao.command.builder;

import com.hyd.dao.mate.util.ConnectionContext;

public abstract class CommandBuilder {

    protected final ConnectionContext context;

    public CommandBuilder(ConnectionContext context) {
        this.context = context;
    }
}

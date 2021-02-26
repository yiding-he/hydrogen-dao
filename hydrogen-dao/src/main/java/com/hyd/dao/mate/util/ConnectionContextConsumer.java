package com.hyd.dao.mate.util;

import com.hyd.dao.database.ConnectionContext;

import java.sql.SQLException;

@FunctionalInterface
public interface ConnectionContextConsumer {

    void accept(ConnectionContext context) throws SQLException;
}

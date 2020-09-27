package com.hyd.dao.mate.util;

import java.sql.SQLException;

@FunctionalInterface
public interface ConnectionConsumer {

    void accept(ConnectionContext context) throws SQLException;
}

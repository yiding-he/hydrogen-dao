package com.hyd.dao.spring;

import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.sql.DataSource;
import java.sql.Connection;

public class SpringConnectionFactory {

    public static Connection getConnection(DataSource dataSource) {
        return DataSourceUtils.getConnection(dataSource);
    }

    public static boolean isTransactionActive() {
        return TransactionSynchronizationManager.isSynchronizationActive();
    }
}

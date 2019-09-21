package com.hyd.dao.spring;

import java.sql.Connection;
import javax.sql.DataSource;
import org.springframework.jdbc.datasource.DataSourceUtils;

public class SpringConnectionFactory {

    public static Connection getConnection(DataSource dataSource) {
        return DataSourceUtils.getConnection(dataSource);
    }
}

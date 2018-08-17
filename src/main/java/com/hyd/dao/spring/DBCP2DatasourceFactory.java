package com.hyd.dao.spring;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sql.DataSource;

/**
 * (description)
 * created at 2017/12/25
 *
 * @author yidin
 */
public class DBCP2DatasourceFactory {

    static boolean isAvailable() {
        try {
            Class.forName("org.apache.commons.dbcp2.BasicDataSource");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    static DataSource createDataSource(@Autowired DataSourceConfig dataSourceProperties) {
        BasicDataSource basicDataSource = new BasicDataSource();
        basicDataSource.setDriverClassName(dataSourceProperties.getDriverClassName());
        basicDataSource.setUrl(dataSourceProperties.getUrl());
        basicDataSource.setUsername(dataSourceProperties.getUsername());
        basicDataSource.setPassword(dataSourceProperties.getPassword());
        return basicDataSource;
    }
}

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
public class BasicDataSourceCreator {

    static DataSource createDataSource(@Autowired DataSourceProperties dataSourceProperties) {
        BasicDataSource basicDataSource = new BasicDataSource();
        basicDataSource.setDriverClassName(dataSourceProperties.getDriverClassName());
        basicDataSource.setUrl(dataSourceProperties.getUrl());
        basicDataSource.setUsername(dataSourceProperties.getUsername());
        basicDataSource.setPassword(dataSourceProperties.getPassword());
        return basicDataSource;
    }
}

package com.hyd.dao.spring;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sql.DataSource;

/**
 * (description)
 * created at 2017/12/25
 *
 * @author yidin
 */
public class DruidDataSourceCreator {

    static DataSource createDataSource(@Autowired DataSourceProperties dataSourceProperties) {
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setDriverClassName(dataSourceProperties.getDriverClassName());
        druidDataSource.setUrl(dataSourceProperties.getUrl());
        druidDataSource.setUsername(dataSourceProperties.getUsername());
        druidDataSource.setPassword(dataSourceProperties.getPassword());
        return druidDataSource;
    }
}

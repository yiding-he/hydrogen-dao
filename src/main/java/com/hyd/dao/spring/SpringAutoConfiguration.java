package com.hyd.dao.spring;

import com.alibaba.druid.pool.DruidDataSource;
import com.hyd.dao.DAO;
import com.hyd.dao.DataSources;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.hyd.dao.DataSources.DEFAULT_DATA_SOURCE_NAME;

/**
 * @author yidin
 */
@SuppressWarnings("SpringFacetCodeInspection")
@Configuration
@AutoConfigureOrder()
@ConditionalOnClass(value = {javax.sql.DataSource.class})
@EnableConfigurationProperties(value = DataSourceProperties.class)
public class SpringAutoConfiguration {

    @Bean
    @ConditionalOnMissingClass
    public DataSources dataSources() {
        return new DataSources();
    }

    @Bean
    @ConditionalOnMissingClass
    @ConditionalOnClass(BasicDataSource.class)
    public DAO daoFromDBCP(
            @Autowired DataSourceProperties dataSourceProperties,
            @Autowired DataSources dataSources
    ) {
        BasicDataSource basicDataSource = new BasicDataSource();
        basicDataSource.setDriverClassName(dataSourceProperties.getDriverClassName());
        basicDataSource.setUrl(dataSourceProperties.getUrl());
        basicDataSource.setUsername(dataSourceProperties.getUsername());
        basicDataSource.setPassword(dataSourceProperties.getPassword());

        dataSources.setDataSource(DEFAULT_DATA_SOURCE_NAME, basicDataSource);
        return dataSources.getDAO(DEFAULT_DATA_SOURCE_NAME);
    }

    @Bean
    @ConditionalOnMissingClass
    @ConditionalOnClass(DruidDataSource.class)
    public DAO daoFromDruid(
            @Autowired DataSourceProperties dataSourceProperties,
            @Autowired DataSources dataSources
    ) {
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setDriverClassName(dataSourceProperties.getDriverClassName());
        druidDataSource.setUrl(dataSourceProperties.getUrl());
        druidDataSource.setUsername(dataSourceProperties.getUsername());
        druidDataSource.setPassword(dataSourceProperties.getPassword());

        dataSources.setDataSource(DEFAULT_DATA_SOURCE_NAME, druidDataSource);
        return dataSources.getDAO(DEFAULT_DATA_SOURCE_NAME);
    }
}

package com.hyd.dao.springboot.multidatasource;

import com.hyd.dao.DAO;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class MultiDataSourceConf {

    @Bean("ds1")
    @ConfigurationProperties(prefix = "spring.datasource.ds1")
    public DataSource dataSource1() {
        return DataSourceBuilder.create().build();
    }

    @Bean("ds2")
    @ConfigurationProperties(prefix = "spring.datasource.ds2")
    public DataSource dataSource2() {
        return DataSourceBuilder.create().build();
    }

    @Bean("ds3")
    @ConfigurationProperties(prefix = "spring.datasource.ds3")
    public DataSource dataSource3() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    public DAO dao1() {
        return new DAO("ds1");
    }

    @Bean
    public DAO dao2() {
        return new DAO("ds2");
    }

    @Bean
    public DAO dao3() {
        return new DAO("ds3");
    }
}

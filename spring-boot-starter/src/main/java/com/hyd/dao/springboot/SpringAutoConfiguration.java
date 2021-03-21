package com.hyd.dao.springboot;

import com.hyd.dao.DAO;
import com.hyd.dao.DataSources;
import com.hyd.dao.log.Logger;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

import static com.hyd.dao.DataSources.DEFAULT_DATA_SOURCE_NAME;

/**
 * Spring Boot 的自动化配置，目前只支持单个 DataSource。
 * 需要有以下依赖关系：spring-boot-starter-jdbc
 *
 * @author yidin
 */
@Configuration
@AutoConfigureAfter(DataSourceAutoConfiguration.class)
public class SpringAutoConfiguration {

    private static final Logger LOG = Logger.getLogger(SpringAutoConfiguration.class);

    @Bean
    @ConditionalOnBean(DataSource.class)
    public DAO dao(
        DataSource dataSource
    ) {
        DataSources dataSources = DataSources.getInstance();
        dataSources.setDataSource(DEFAULT_DATA_SOURCE_NAME, dataSource);
        DAO dao = new DAO(DEFAULT_DATA_SOURCE_NAME);
        LOG.info("DAO '" + DEFAULT_DATA_SOURCE_NAME + "' initialized as " + dao);
        return dao;
    }

}

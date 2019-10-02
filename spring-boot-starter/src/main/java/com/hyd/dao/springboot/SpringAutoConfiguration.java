package com.hyd.dao.springboot;

import com.hyd.dao.DAO;
import com.hyd.dao.DataSources;
import com.hyd.dao.log.Logger;
import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 * Spring Boot 的自动化配置，目前只支持单个 DataSource。
 * 需要有以下依赖关系：spring-boot-starter-jdbc
 *
 * @author yidin
 */
@Configuration
@ImportAutoConfiguration(DataSourceAutoConfiguration.class)
@Order
public class SpringAutoConfiguration {

    private static final Logger LOG = Logger.getLogger(SpringAutoConfiguration.class);

    public static final String DEFAULT_DATA_SOURCES_BEAN_NAME = "dataSources";

    public static final String DEFAULT_DAO_BEAN_NAME = "dao";

    @Bean(DEFAULT_DATA_SOURCES_BEAN_NAME)
    public DataSources dataSources() {
        LOG.debug("DataSources initialized.");
        return new DataSources();
    }

    @Bean(DEFAULT_DAO_BEAN_NAME)
    @ConditionalOnBean(DataSource.class)
    public DAO dao(
        DataSource dataSource,
        DataSources dataSources
    ) {
        dataSources.setDataSource(DataSources.DEFAULT_DATA_SOURCE_NAME, dataSource);
        return dataSources.getDAO(DataSources.DEFAULT_DATA_SOURCE_NAME);
    }

}

package com.hyd.dao.spring;

import static com.hyd.dao.DataSources.DEFAULT_DATA_SOURCE_NAME;

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
 * 需要有以下依赖关系：spring-boot-autoconfigure, spring-jdbc
 *
 * @author yidin
 */
@Configuration
@ImportAutoConfiguration(DataSourceAutoConfiguration.class)
@Order
public class SpringAutoConfiguration {

    private static final Logger LOG = Logger.getLogger(SpringAutoConfiguration.class);

    @Bean
    public DataSources dataSources() {
        LOG.debug("DataSources initialized.");
        return new DataSources();
    }

    @Bean
    @ConditionalOnBean(DataSource.class)
    public DAO dao(
        DataSource dataSource,
        DataSources dataSources
    ) {
        dataSources.setDataSource(DEFAULT_DATA_SOURCE_NAME, dataSource);
        return dataSources.getDAO(DEFAULT_DATA_SOURCE_NAME);
    }

}

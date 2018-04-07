package com.hyd.dao.spring;

import com.hyd.dao.DAO;
import com.hyd.dao.DataSources;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

import static com.hyd.dao.DataSources.DEFAULT_DATA_SOURCE_NAME;
import static org.springframework.util.StringUtils.hasText;

/**
 * @author yidin
 */
@Configuration
@AutoConfigureOrder()
@ConditionalOnMissingBean(DAO.class)
@ConditionalOnClass(value = {javax.sql.DataSource.class})
@ConditionalOnProperty("spring.datasource.url")
@EnableConfigurationProperties(value = DataSourceProperties.class)
public class SpringAutoConfiguration {

    @Bean
    @ConditionalOnMissingClass
    public DataSources dataSources() {
        return new DataSources();
    }

    @Bean
    @ConditionalOnClass(name = "org.apache.commons.dbcp2.BasicDataSource")
    public DAO daoFromDBCP(
            DataSourceProperties dataSourceProperties,
            DataSources dataSources
    ) {

        if (!hasText(dataSourceProperties.getDriverClassName()) ||
                !hasText(dataSourceProperties.getUrl()) ||
                !hasText(dataSourceProperties.getUsername())) {
            return null;
        }

        DataSource basicDataSource = BasicDataSourceCreator.createDataSource(dataSourceProperties);

        dataSources.setDataSource(DEFAULT_DATA_SOURCE_NAME, basicDataSource);
        return dataSources.getDAO(DEFAULT_DATA_SOURCE_NAME);
    }

}

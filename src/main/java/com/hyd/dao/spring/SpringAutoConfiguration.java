package com.hyd.dao.spring;

import com.hyd.dao.DAO;
import com.hyd.dao.DataSources;
import com.hyd.dao.database.JDBCDriver;
import com.hyd.dao.log.Logger;
import com.hyd.dao.util.Str;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
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
@ConditionalOnProperty("spring.datasource.url")
@EnableConfigurationProperties(value = DataSourceProperties.class)
public class SpringAutoConfiguration {

    private static final Logger LOG = Logger.getLogger(SpringAutoConfiguration.class);

    @Bean
    @ConditionalOnMissingBean(DataSources.class)
    public DataSources dataSources() {
        return new DataSources();
    }

    @Bean
    public DAO dao(
            DataSourceProperties props,
            DataSources dataSources
    ) {

        if (!hasText(props.getUrl()) ||
                !hasText(props.getUsername())) {
            return null;
        }

        JDBCDriver driver = JDBCDriver.getDriverByUrl(props.getUrl());
        if (Str.isEmpty(props.getDriverClassName())) {
            if (driver != null) {
                props.setDriverClassName(driver.getDriverClass());
            } else {
                LOG.info("bean 'dao' not initialized: missing driver class");
            }
        }

        DataSource dataSource;

        if (DBCP2DatasourceFactory.isAvailable()) {
            dataSource = DBCP2DatasourceFactory.createDataSource(props);
        } else if (DruidDataSourceFactory.isAvailable()) {
            dataSource = DruidDataSourceFactory.createDataSource(props);
        } else {
            LOG.warn("Warning: using non-pooled datasource, " +
                    "do not use this in production environment!");
            dataSource = NonPooledDataSourceFactory.createDataSource(props);
        }

        if (dataSource == null) {
            LOG.info("bean 'dao' not initialized: ");
            return null;
        } else {
            LOG.info("bean 'dao' initialized as " + dataSource.getClass() + ".");
        }

        dataSources.setDataSource(DEFAULT_DATA_SOURCE_NAME, dataSource);
        return dataSources.getDAO(DEFAULT_DATA_SOURCE_NAME);
    }

}

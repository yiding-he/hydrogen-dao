package com.hyd.dao.spring;

import com.hyd.dao.DAO;
import com.hyd.dao.DataSources;
import com.hyd.dao.database.JDBCDriver;
import com.hyd.dao.log.Logger;
import com.hyd.dao.util.Str;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.Map;

import static com.hyd.dao.DataSources.DEFAULT_DATA_SOURCE_NAME;
import static org.springframework.util.StringUtils.hasText;

/**
 * @author yidin
 */
@Configuration
@AutoConfigureOrder()
@ConditionalOnMissingBean(DAO.class)
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

        Map<String, DataSourceConfig> dataSourceConfigs = props.getDataSources();
        if (dataSourceConfigs == null || dataSourceConfigs.isEmpty()) {
            return null;
        }

        dataSourceConfigs.forEach((dataSourceName, dataSourceConfig) ->
                setupDao(dataSourceName, dataSourceConfig, dataSources));

        return dataSources.getDAO(DEFAULT_DATA_SOURCE_NAME);
    }

    private void setupDao(
            String dataSourceName, DataSourceConfig config, DataSources dataSources) {

        if (!hasText(config.getUrl()) ||
                !hasText(config.getUsername())) {
            return;
        }

        JDBCDriver driver = JDBCDriver.getDriverByUrl(config.getUrl());
        if (Str.isEmpty(config.getDriverClassName())) {
            if (driver != null) {
                config.setDriverClassName(driver.getDriverClass());
            } else {
                LOG.info("bean 'dao' not initialized: missing driver class");
            }
        }

        DataSource dataSource;

        if (DBCP2DatasourceFactory.isAvailable()) {
            dataSource = DBCP2DatasourceFactory.createDataSource(config);
        } else if (DruidDataSourceFactory.isAvailable()) {
            dataSource = DruidDataSourceFactory.createDataSource(config);
        } else {
            LOG.warn("Warning: using non-pooled datasource, " +
                    "do not use this in production environment!");
            dataSource = NonPooledDataSourceFactory.createDataSource(config);
        }

        if (dataSource == null) {
            return;
        } else {
            LOG.info(() -> "DAO instance '" + dataSourceName + "' initiated.");
        }

        dataSources.setDataSource(dataSourceName, dataSource);
    }

}

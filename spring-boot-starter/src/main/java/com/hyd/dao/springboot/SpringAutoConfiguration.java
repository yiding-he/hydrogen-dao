package com.hyd.dao.springboot;

import com.hyd.dao.DAO;
import com.hyd.dao.DataSources;
import com.hyd.dao.log.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

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
    @ConditionalOnMissingBean
    public DataSources dataSources() {
        return DataSources.getInstance();
    }

    @Bean("dao")
    @ConditionalOnMissingBean(name = "dao")
    public DAO dao() {
        return new DAO(DataSources.DEFAULT_DATA_SOURCE_NAME);
    }

    @Bean
    public BeanPostProcessor dataSourcePostProcessor() {
        return new BeanPostProcessor() {
            @Override
            public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
                if (bean instanceof DataSource ds) {
                    var instance = DataSources.getInstance();
                    if (instance.isEmpty()) {
                        instance.setDataSource(DataSources.DEFAULT_DATA_SOURCE_NAME, ds);
                    }
                    instance.setDataSource(beanName, ds);
                    LOG.debug("Recognized data source '" + beanName + "' as " + ds);
                }
                return bean;
            }
        };
    }
}

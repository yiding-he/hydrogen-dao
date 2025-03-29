package com.hyd.dao.spring;

import com.hyd.dao.DAO;
import com.hyd.dao.DataSources;
import com.hyd.dao.log.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;

/**
 * 自动根据 spring.datasource 配置初始化 DAO 对象
 * 使用方法：在适当的地方加入 <code>@Import(com.hyd.dao.spring.SpringAutoConfiguration.class)</code> 即可。
 * 多数据源：这里会自动根据数据源对象的 beanName 注册到 DataSources 中，以便后面创建各自不同的 DAO 对象。
 *
 * @author yidin
 */
@AutoConfigureAfter(DataSourceAutoConfiguration.class)
public class SpringAutoConfiguration {

    private static final Logger LOG = Logger.getLogger(SpringAutoConfiguration.class);

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

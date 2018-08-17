package com.hyd.dao.spring;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties(
        prefix = "hydrogen-dao"
)
public class DataSourceProperties {

    private Map<String, DataSourceConfig> dataSources;

    public Map<String, DataSourceConfig> getDataSources() {
        return dataSources;
    }

    public void setDataSources(Map<String, DataSourceConfig> dataSources) {
        this.dataSources = dataSources;
    }
}

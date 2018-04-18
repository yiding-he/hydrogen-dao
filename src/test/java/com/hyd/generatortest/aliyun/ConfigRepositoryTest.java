package com.hyd.generatortest.aliyun;

import com.hyd.dao.DataSources;
import com.hyd.dao.util.DBCPDataSource;
import org.apache.commons.dbcp2.BasicDataSource;

/**
 * @author yiding.he
 */
public class ConfigRepositoryTest {

    public static void main(String[] args) {
        BasicDataSource dataSource = DBCPDataSource.newMySqlDataSource(
                "znxunzhi-dev-testing.mysql.rds.aliyuncs.com", 3527, "zkcf_dev",
                "zkcf_dev", "zkcf_DEV", true, "UTF-8");

        DataSources dataSources = new DataSources();
        dataSources.setDataSource(DataSources.DEFAULT_DATA_SOURCE_NAME, dataSource);

        ConfigRepository configRepository = new ConfigRepository();
        configRepository.setDao(dataSources.getDAO(DataSources.DEFAULT_DATA_SOURCE_NAME));

        Config config = configRepository.queryByTypeAndConfigKey("111", "222");
        System.out.println(config);
    }
}

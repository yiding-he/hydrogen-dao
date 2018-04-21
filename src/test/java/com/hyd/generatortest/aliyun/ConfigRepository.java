package com.hyd.generatortest.aliyun;

import com.hyd.dao.DAO;
import com.hyd.dao.SQL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ConfigRepository {

    @Autowired
    private DAO dao;

    public void setDao(DAO dao) {
        this.dao = dao;
    }

    public Config queryByTypeAndConfigKey(String type, String configKey) {
        return dao.queryFirst(Config.class, 
            SQL.Select("*")
            .From("config")
            .Where("type = ?", type)
            .And("config_key = ?", configKey)
        );
    }

}
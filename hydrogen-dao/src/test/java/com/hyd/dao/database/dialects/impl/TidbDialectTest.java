package com.hyd.dao.database.dialects.impl;

import com.hyd.dao.DAO;
import com.hyd.dao.DataSources;
import com.hyd.daotests.DataSourceFactories;
import org.junit.Test;

public class TidbDialectTest {

    @Test
    public void testGetMatcher() {
        var dataSources = DataSources.getInstance();
        dataSources.setDataSource(
            DataSources.DEFAULT_DATA_SOURCE_NAME,
            DataSourceFactories.getDataSource(DataSourceFactories.LOCAL_HOST_TIDB)
        );
        var dao = new DAO(DataSources.DEFAULT_DATA_SOURCE_NAME);
        var rows = dao.queryRange(
            "select * from data_project order by data_project_id desc", 0, 10);

        for (var row : rows) {
            System.out.println(row);
        }
    }
}

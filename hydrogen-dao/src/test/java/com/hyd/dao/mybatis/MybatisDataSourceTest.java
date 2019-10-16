package com.hyd.dao.mybatis;

import com.hyd.dao.DAO;
import com.hyd.dao.DataSources;
import javax.sql.DataSource;
import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.junit.Test;

public class MybatisDataSourceTest {

    @Test
    public void testCreateInMemoryDataSource() throws Exception {
        DataSource dataSource = new PooledDataSource(
            "org.h2.Driver","jdbc:h2:mem:",null,null
        );

        DataSources dataSources = new DataSources();
        dataSources.setDataSource(DataSources.DEFAULT_DATA_SOURCE_NAME, dataSource);

        DAO dao = dataSources.getDAO(DataSources.DEFAULT_DATA_SOURCE_NAME);
        System.out.println(dao.query("select current_timestamp()"));
    }
}

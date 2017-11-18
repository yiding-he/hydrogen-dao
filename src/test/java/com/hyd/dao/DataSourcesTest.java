package com.hyd.dao;

import com.hyd.dao.util.DBCPDataSource;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 * (description)
 * created at 2017/11/16
 *
 * @author yidin
 */
public class DataSourcesTest {

    @Test
    public void getDAO() throws Exception {
        DataSources dataSources = new DataSources();
        dataSources.setDataSource("111", DBCPDataSource.newMySqlDataSource(
                "localhost", 3306, "db1", "db1user", "db1pass", true, "UTF-8"));

        assertNotNull(dataSources.getDAO("111"));
        assertNotNull(dataSources.getDAO("111", true));

        class MyDAO extends DAO {

        }

        MyDAO myDAO = dataSources.getDAO("111", true, MyDAO.class);
        assertNotNull(myDAO);
    }

}
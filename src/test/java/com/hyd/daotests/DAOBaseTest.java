package com.hyd.daotests;

import com.hyd.dao.DAO;
import com.hyd.dao.DataSources;
import com.hyd.dao.util.DBCPDataSource;
import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.BeforeClass;

/**
 * @author yidin
 */
public abstract class DAOBaseTest {

    private static DataSources dataSources = new DataSources();

    protected static DAO dao;

    @BeforeClass
    public static void beforeClass() {
        BasicDataSource dataSource = DBCPDataSource.newH2MemDataSource();
        dataSources.setDataSource("h2", dataSource);
        dao = dataSources.getDAO("h2");
    }
}

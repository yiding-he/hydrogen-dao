package com.hyd.daotests;

import com.hyd.dao.DAO;
import com.hyd.dao.DataSources;
import com.hyd.dao.util.DBCPDataSource;
import com.hyd.dao.util.ScriptExecutor;
import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.Before;
import org.junit.BeforeClass;

/**
 * @author yidin
 */
public abstract class DAOBaseTest {

    private static DataSources dataSources = new DataSources();

    protected static DAO dao;

    @BeforeClass
    public static void beforeClass() throws Exception {
        BasicDataSource dataSource = DBCPDataSource.newH2MemDataSource();
        dataSources.setDataSource("h2", dataSource);
        dao = dataSources.getDAO("h2");

        ScriptExecutor.execute("/scripts/tables.sql", dao);
    }

    @Before
    public void clearData() {
        dao.execute("truncate table blog");
    }
}

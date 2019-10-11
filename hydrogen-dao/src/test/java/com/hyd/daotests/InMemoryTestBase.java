package com.hyd.daotests;

import com.hyd.dao.DAO;
import com.hyd.dao.DataSources;
import com.hyd.dao.database.type.NameConverter;
import com.hyd.dao.mate.util.DBCPDataSource;
import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.BeforeClass;

/**
 * @author yidin
 */
public abstract class InMemoryTestBase {

    private static DataSources dataSources = new DataSources();

    protected static DAO dao;

    protected static DAO dao2;

    @BeforeClass
    public static void beforeClass() {
        BasicDataSource dataSource = DBCPDataSource.newH2MemDataSource();
        dataSources.setDataSource("h20", dataSource);
        dataSources.setDataSource("h21", dataSource);

        dataSources.setColumnNameConverter("h21", NameConverter.NONE);

        dao = dataSources.getDAO("h20");
        dao2 = dataSources.getDAO("h21");
    }
}

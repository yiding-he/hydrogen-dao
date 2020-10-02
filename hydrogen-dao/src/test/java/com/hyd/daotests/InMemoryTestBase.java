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

    private static DataSources dataSources = DataSources.getInstance();

    protected static DAO dao;

    protected static DAO dao2;

    protected static BasicDataSource dataSource;

    @BeforeClass
    public static void beforeClass() {
        dataSource = DBCPDataSource.newH2MemDataSource();
        dataSources.setDataSource("h20", dataSource);
        dataSources.setDataSource("h21", dataSource);

        dataSources.setColumnNameConverter("h21", NameConverter.NONE);

        dao = new DAO("h20");
        dao2 = new DAO("h21");
    }
}

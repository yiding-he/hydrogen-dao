package com.hyd.daotests.dialects;

import com.hyd.dao.DAO;
import com.hyd.dao.mate.util.DBCPDataSource;
import com.hyd.daotests.AbstractTestBase;
import com.hyd.daotests.scenarios.BasicDAOTest;
import com.hyd.daotests.scenarios.RepositoryTest;
import com.hyd.daotests.scenarios.ScriptExecutionTest;

import javax.sql.DataSource;

public class H2Test extends AbstractTestBase
    implements BasicDAOTest, ScriptExecutionTest, RepositoryTest {

    @Override
    protected DataSource createDataSource() {
        return DBCPDataSource.newH2MemDataSource();
    }

    @Override
    public DAO getDao() {
        return dao;
    }
}

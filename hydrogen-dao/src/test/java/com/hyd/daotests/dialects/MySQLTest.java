package com.hyd.daotests.dialects;

import com.hyd.dao.DAO;
import com.hyd.daotests.AbstractTestBase;
import com.hyd.daotests.DataSourceFactories;
import com.hyd.daotests.scenarios.BasicDAOTest;
import com.hyd.daotests.scenarios.RepositoryTest;
import com.hyd.daotests.scenarios.ScriptExecutionTest;
import org.apache.commons.dbcp2.BasicDataSource;

import javax.sql.DataSource;

public class MySQLTest extends AbstractTestBase
    implements BasicDAOTest, ScriptExecutionTest, RepositoryTest {

    @Override
    protected DataSource createDataSource() {
        DataSource dataSource = DataSourceFactories.mysqlDataSource();
        ((BasicDataSource)dataSource).setMaxTotal(3);
        return dataSource;
    }

    @Override
    public DAO getDao() {
        return dao;
    }
}

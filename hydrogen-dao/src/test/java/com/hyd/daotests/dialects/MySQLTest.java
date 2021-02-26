package com.hyd.daotests.dialects;

import com.hyd.daotests.AbstractDaoTest;
import com.hyd.daotests.DataSourceFactories;

import javax.sql.DataSource;

public class MySQLTest extends AbstractDaoTest {

    @Override
    protected DataSource createDataSource() {
        return DataSourceFactories.mysqlDataSource();
    }
}

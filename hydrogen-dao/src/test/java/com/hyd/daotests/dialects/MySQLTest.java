package com.hyd.daotests.dialects;

import com.hyd.daotests.AbstractDaoTest;
import com.hyd.daotests.DataSourceFactories;
import org.apache.commons.dbcp2.BasicDataSource;

import javax.sql.DataSource;

import static com.hyd.daotests.DataSourceFactories.LOCAL_HOST_3306;

public class MySQLTest extends AbstractDaoTest {

    @Override
    protected DataSource getDataSource() {
        DataSource dataSource = DataSourceFactories.getDataSource(LOCAL_HOST_3306);
        ((BasicDataSource)dataSource).setMaxTotal(POOL_SIZE);
        return dataSource;
    }
}

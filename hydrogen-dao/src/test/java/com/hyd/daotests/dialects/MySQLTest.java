package com.hyd.daotests.dialects;

import com.hyd.daotests.AbstractDaoTest;
import com.hyd.daotests.DataSourceFactories;
import org.apache.commons.dbcp2.BasicDataSource;

import javax.sql.DataSource;
import java.sql.SQLException;

public class MySQLTest extends AbstractDaoTest {

    @Override
    protected void closeDataSource(DataSource dataSource) throws SQLException {
        if (dataSource instanceof BasicDataSource bds) {
            bds.close();
        }
    }

    @Override
    protected DataSource createDataSource() {
        DataSource dataSource = DataSourceFactories.mysqlDataSource();
        ((BasicDataSource)dataSource).setMaxTotal(3);
        return dataSource;
    }
}

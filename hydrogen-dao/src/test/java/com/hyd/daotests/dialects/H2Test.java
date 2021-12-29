package com.hyd.daotests.dialects;

import com.hyd.dao.mate.util.DBCPDataSource;
import com.hyd.daotests.AbstractDaoTest;
import org.apache.commons.dbcp2.BasicDataSource;

import javax.sql.DataSource;
import java.sql.SQLException;

public class H2Test extends AbstractDaoTest {

    @Override
    protected DataSource createDataSource() {
        return DBCPDataSource.newH2MemDataSource();
    }

    @Override
    protected void closeDataSource(DataSource dataSource) throws SQLException {
        if (dataSource instanceof BasicDataSource bds) {
            bds.close();
        }
    }
}

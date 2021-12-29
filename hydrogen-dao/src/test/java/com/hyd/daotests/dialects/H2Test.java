package com.hyd.daotests.dialects;

import com.hyd.dao.mate.util.DBCPDataSource;
import com.hyd.daotests.AbstractDaoTest;

import javax.sql.DataSource;

public class H2Test extends AbstractDaoTest {

    @Override
    protected DataSource getDataSource() {
        return DBCPDataSource.newH2MemDataSource();
    }
}

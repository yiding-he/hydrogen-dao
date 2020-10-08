package com.hyd.daotests;

import com.hyd.dao.DAO;
import com.hyd.dao.DataSources;
import com.hyd.dao.junit.HydrogenDAORule;
import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.Rule;

import static com.hyd.dao.mate.util.DBCPDataSource.newMySqlDataSource;

public abstract class MySQLTestBase {

    static {
        BasicDataSource dataSource = newMySqlDataSource(
            "localhost", 4000, "test", "root", "root123", true, "utf8"
        );
        DataSources dataSources = DataSources.getInstance();
        dataSources.setDataSource("default", dataSource);
    }

    protected final DAO dao = new DAO("default");

    @Rule
    public HydrogenDAORule hydrogenDAORule = new HydrogenDAORule(() -> this.dao);
}

package com.hyd.daotests;

import com.hyd.dao.DAO;
import com.hyd.dao.DataSources;
import com.hyd.dao.junit.HydrogenDAORule;

import javax.sql.DataSource;
import java.util.function.Supplier;

/**
 * @author yidin
 */
public abstract class JUnitRuleTestBase {

    protected DAO dao;

    protected abstract DataSource createDataSource();

    {
        if (!DataSources.getInstance().contains("default")) {
            DataSources.getInstance().setDataSource("default", createDataSource());
        }
        this.dao = new DAO("default");
    }

    public HydrogenDAORule hydrogenDAORule = new HydrogenDAORule(getDAOSupplier());

    protected Supplier<DAO> getDAOSupplier() {
        return this::getDao;
    }

    protected DAO getDao() {
        return this.dao;
    }
}

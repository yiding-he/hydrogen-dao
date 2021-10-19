package com.hyd.daotests;

import com.hyd.dao.DAO;
import com.hyd.dao.DataSources;
import com.hyd.dao.junit.HydrogenDAORule;
import org.junit.Rule;

import javax.sql.DataSource;
import java.util.function.Supplier;

/**
 * @author yidin
 */
public abstract class JUnitRuleTestBase {

    protected DAO dao;

    /**
     * get or create data source
     */
    protected abstract DataSource getDataSource();

    {
        if (!DataSources.getInstance().contains("default")) {
            DataSources.getInstance().setDataSource("default", getDataSource());
        }
        this.dao = new DAO("default");
    }

    @Rule
    public HydrogenDAORule hydrogenDAORule = new HydrogenDAORule(getDAOSupplier());

    protected Supplier<DAO> getDAOSupplier() {
        return this::getDao;
    }

    protected DAO getDao() {
        return this.dao;
    }

    protected void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

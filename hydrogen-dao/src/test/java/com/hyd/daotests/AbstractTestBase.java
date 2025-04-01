package com.hyd.daotests;

import com.hyd.dao.DAO;
import com.hyd.dao.DataSources;
import com.hyd.dao.junit.HydrogenDAORule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import javax.sql.DataSource;

import static com.hyd.dao.DataSources.DEFAULT_DATA_SOURCE_NAME;

public abstract class AbstractTestBase implements TestBase {

    protected DAO dao;

    protected HydrogenDAORule rule;

    @BeforeEach
    public void init() {
        if (!DataSources.getInstance().contains(DEFAULT_DATA_SOURCE_NAME)) {
            DataSources.getInstance().setDataSource(DEFAULT_DATA_SOURCE_NAME, createDataSource());
        }
        this.dao = new DAO(DEFAULT_DATA_SOURCE_NAME);
        this.rule = new HydrogenDAORule(this::getDao);
        this.rule.before();
    }

    @AfterEach
    public void fin() {
        this.rule.after();
    }

    protected abstract DataSource createDataSource();

}

package com.hyd.daotests;

import com.hyd.dao.DAO;
import com.hyd.dao.junit.HydrogenDAORule;
import java.util.function.Supplier;
import org.junit.Rule;

/**
 * @author yidin
 */
public abstract class JUnitRuleTestBase extends InMemoryTestBase {

    @Rule
    public HydrogenDAORule hydrogenDAORule = new HydrogenDAORule(getDAOSupplier());

    protected abstract Supplier<DAO> getDAOSupplier();
}

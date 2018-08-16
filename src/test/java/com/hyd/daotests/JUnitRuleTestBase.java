package com.hyd.daotests;

import com.hyd.dao.junit.HydrogenDAORule;
import org.junit.Rule;

/**
 * @author yidin
 */
public class JUnitRuleTestBase extends InMemoryTestBase {

    @Rule
    public HydrogenDAORule hydrogenDAORule = new HydrogenDAORule(() -> dao);
}

package com.hyd.daotests;

import com.hyd.dao.unittest.HydrogenDAORule;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author yidin
 */
public class JUnitRuleTest extends DAOBaseTest {

    @Rule
    public HydrogenDAORule hydrogenDAORule = new HydrogenDAORule(() -> dao);

    @Test
    public void testQuery() {
        Assert.assertEquals(3, dao.count("select count(*) from blog"));
    }
}

package com.hyd.dao.database.executor;

import com.hyd.dao.database.ExecutorFactory;
import com.hyd.daotests.JUnitRuleTestBase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class DefaultExecutorTest extends JUnitRuleTestBase {

    private Executor executor;

    @Before
    public void init() {
        this.executor = ExecutorFactory.getExecutor(dao);
    }

    @Test
    public void testClose() throws Exception {
        Assert.assertFalse(this.executor.isClosed());
        this.executor.finish();
        Assert.assertTrue(this.executor.isClosed());
    }
}

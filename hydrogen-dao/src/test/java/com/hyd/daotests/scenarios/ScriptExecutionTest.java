package com.hyd.daotests.scenarios;

import com.hyd.dao.mate.util.ScriptExecutor;
import com.hyd.daotests.TestBase;
import org.junit.jupiter.api.Test;

public interface ScriptExecutionTest extends TestBase {

    @Test
    default void testExecute() throws Exception {
        ScriptExecutor.execute("classpath:/scripts/tables.sql", getDao());
    }
}

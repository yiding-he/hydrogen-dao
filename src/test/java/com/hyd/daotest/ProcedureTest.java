package com.hyd.daotest;

import com.hyd.dao.DAO;
import org.junit.Test;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;

/**
 * @author yiding.he
 */
public class ProcedureTest extends BaseTest {

    @Test
    public void testProcedure() throws Exception {
        DAO dao = getDAO();
        List result = dao.call("add_one", 1);

        assertFalse(result.isEmpty());
        assertEquals(2, Integer.parseInt(result.get(0).toString()));
    }
}

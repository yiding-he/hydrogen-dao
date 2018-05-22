package com.hyd.dao.util;

import com.hyd.dao.DAO;
import com.hyd.dao.DAOUtils;
import org.junit.Test;

/**
 * (description)
 * created at 2018/5/22
 *
 * @author yidin
 */
public class ScriptExecutorTest {

    static {
        DAOUtils.setupLocalMySQL();
    }

    @Test
    public void testExecute() throws Exception {
        DAO dao = DAOUtils.getDAO();
        ScriptExecutor.execute("classpath:/junit-test-scripts/init.sql", dao);
    }
}
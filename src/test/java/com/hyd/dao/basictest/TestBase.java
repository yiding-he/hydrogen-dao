package com.hyd.dao.basictest;

import com.hyd.dao.DAO;
import com.hyd.dao.DAOUtils;
import com.hyd.dao.util.ScriptExecutor;
import org.junit.BeforeClass;

/**
 * (description)
 * created at 2018/4/23
 *
 * @author yidin
 */
public class TestBase {

    {
        DAOUtils.setupLocalMySQL();
    }

    DAO getDAO() {
        return DAOUtils.getDAO();
    }

    @BeforeClass
    public static void init() {
        ScriptExecutor.execute("/junit-test-scripts/init.sql", DAOUtils.getDAO());
    }
}

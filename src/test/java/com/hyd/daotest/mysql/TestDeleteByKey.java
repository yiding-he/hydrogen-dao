package com.hyd.daotest.mysql;

import com.hyd.dao.DAO;
import com.hyd.dao.DataSources;
import org.junit.Test;

/**
 * todo: description
 *
 * @author yiding.he
 */
public class TestDeleteByKey {

    @Test
    public void testDeleteByKey() throws Exception {
        DataSources dsManager = Init.initDataSource("localhost");
        DAO dao = dsManager.getDAO("0");


        dao.deleteByKey(1, "t_user");
    }
}

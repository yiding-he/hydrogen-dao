package com.hyd.daotest;

import com.hyd.dao.DAO;
import com.hyd.dao.Page;
import com.hyd.dao.Row;
import com.hyd.dao.SQL;
import com.hyd.daotest.bean.User;
import org.junit.Test;

public class PageDemo extends BaseTest {

    @Test
    public void testQueryPage() throws Exception {
        DAO dao = getDAO();
        Page<Row> page = dao.queryPage(
                SQL.Select("*").From("USERS").Where("USERNAME>?", "user023"), 10, 0);

        System.out.println("Total: " + page.getTotal());

        for (Row row : page) {
            System.out.println(row);
        }
    }
}

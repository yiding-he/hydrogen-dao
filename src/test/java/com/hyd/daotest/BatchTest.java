package com.hyd.daotest;

import com.hyd.dao.BatchCommand;
import com.hyd.dao.DAO;
import com.hyd.dao.Row;
import com.hyd.daotest.bean.User;
import org.junit.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static junit.framework.Assert.assertEquals;

/**
 * @author yiding.he
 */
public class BatchTest extends BaseTest {

    @Test
    public void testBatchInsert() throws Exception {
        DAO dao = getDAO();

        List<User> users = Arrays.asList(
                new User(51L, "haha1", "pp", new Date()),
                new User(52L, "haha2", "pp", new Date()),
                new User(53L, "haha3", "pp", new Date()),
                new User(54L, "haha4", "pp", new Date()),
                new User(55L, "haha5", "pp", new Date())
        );

        dao.insert(users, "USERS");

        List<Row> rows = dao.query("select * from USERS where ID between 50 and 60 order by ID");
        assertEquals(5, rows.size());

        Row row = rows.get(0);
        assertEquals(51, row.getInteger("ID", -1));
        assertEquals("haha1", row.getString("username"));
        assertEquals("pp", row.getString("password"));
    }

    @Test
    public void testBatchUpdate() throws Exception {

        BatchCommand batchCommand = new BatchCommand("update USERS set USERNAME=? where USERNAME=?");
        batchCommand.addParams("newuser1", "user1");
        batchCommand.addParams("newuser2", "user2");
        batchCommand.addParams("newuser3", "user3");
        batchCommand.addParams("newuser4", "user4");
        batchCommand.addParams("newuser5", "user5");

        DAO dao = getDAO();
        assertEquals(0, dao.query("select * from USERS where USERNAME like 'newuser%'").size());
        dao.execute(batchCommand);
        assertEquals(5, dao.query("select * from USERS where USERNAME like 'newuser%'").size());
    }

    @Test
    public void testBatchDelete() throws Exception {

        BatchCommand batchCommand = new BatchCommand("delete from USERS where USERNAME=?");
        batchCommand.addParams("user1");
        batchCommand.addParams("user2");
        batchCommand.addParams("user3");
        batchCommand.addParams("user4");
        batchCommand.addParams("user5");

        String sql = "select * from USERS where USERNAME between 'user1' and 'user5'";
        DAO dao = getDAO();
        assertEquals(5, dao.query(sql).size());
        dao.execute(batchCommand);
        assertEquals(0, dao.query(sql).size());
    }
}

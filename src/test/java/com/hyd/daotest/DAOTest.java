package com.hyd.daotest;

import com.hyd.dao.DAO;
import com.hyd.dao.Row;
import com.hyd.dao.database.RowIterator;
import com.hyd.dao.log.Logger;
import com.hyd.daotest.bean.LobRecord;
import com.hyd.daotest.bean.User;
import org.junit.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.*;

/**
 * @author yiding.he
 */
public class DAOTest extends BaseTest {

    static {
        org.apache.log4j.BasicConfigurator.configure();
    }

    @Test
    public void testSimpleQuery() throws Exception {
        DAO dao = dataSources.getDAO("test");
        List<Row> users = dao.query("select * from USERS");

        assertFalse(users.isEmpty());
        assertEquals(INITIAL_ROWS_COUNT, users.size());
        for (Row user : users) {
            System.out.println(user);
        }
    }

    @Test
    public void testParameterizedQuery() throws Exception {
        DAO dao = dataSources.getDAO("test");

        List<Row> users = dao.query("select * from USERS where USERNAME=?", "user001");
        assertFalse(users.isEmpty());
        assertEquals(1, users.size());

        users = dao.query("select * from USERS where REGISTER_TIME<?", new Date(System.currentTimeMillis() + 100000));
        assertFalse(users.isEmpty());
        assertEquals(INITIAL_ROWS_COUNT, users.size());

        users = dao.query("select * from USERS where USERNAME between ? and ?", "user002", "user007");
        assertFalse(users.isEmpty());
        assertEquals(6, users.size());
    }

    @Test
    public void testQueryRange() throws Exception {
        DAO dao = dataSources.getDAO("test");
        List<Row> users = dao.queryRange("select * from USERS where USERNAME>? order by USERNAME", 5, 15, "user001");
        assertFalse(users.isEmpty());
        assertEquals(10, users.size());
        assertEquals("user007", users.get(0).getString("USERNAME"));
    }

    @Test
    public void testQueryBean() throws Exception {
        DAO dao = dataSources.getDAO("test");

        List<User> users = dao.query(User.class, "select * from USERS");
        assertFalse(users.isEmpty());
        assertEquals(INITIAL_ROWS_COUNT, users.size());

        User user = users.get(0);
        assertNotNull(user);
        assertEquals("user001", user.getUsername());
        assertEquals("pass1", user.getPassword());
        assertNotNull(user.getRegisterTime());
    }

    @Test
    public void testInsertMap() throws Exception {
        DAO dao = getDAO();

        Map<String, Object> user = new HashMap<String, Object>();
        Long id = 0L;
        user.put("id", id);
        user.put("username", "admin1");
        user.put("register_time", new Date());
        user.put("other_properties", "blah blah");  // 表中不存在的字段不影响插入

        dao.insert(user, "USERS");
        List<Row> rows = dao.query("select * from USERS where username=?", "admin1");
        assertEquals(1, rows.size());

        Row row = rows.get(0);
        assertEquals("admin1", row.getString("username"));
        assertNull(row.getString("password"));
        assertNotNull(row.getDate("register_time"));
    }

    @Test
    public void testInsertBean() throws Exception {
        DAO dao = getDAO();
        Long id = 0L;

        User user = new User();
        user.setId(id);
        user.setPassword("adminpass2");
        user.setUsername("admin2");
        user.setBirthday(new Date());
        user.setRegisterTime(DAO.SYSDATE);
        user.setLoginCount(111);

        dao.insert(user);
        List<Row> rows = dao.query("select * from USERS where username=?", "admin2");
        assertEquals(1, rows.size());

        Row row = rows.get(0);
        assertEquals("admin2", row.getString("username"));
        assertEquals("adminpass2", row.getString("password"));
        assertNotNull(row.getDate("register_time"));
    }

    @Test
    public void testDelete() throws Exception {
        DAO dao = getDAO();
        String username = String.format("user%03d", INITIAL_ROWS_COUNT - 1);

        List<Row> rows = dao.query("select * from USERS where username=?", username);
        assertEquals(1, rows.size());

        int count = dao.deleteByKey(rows.get(0).getIntegerObject("ID"), "USERS");
        assertEquals(1, count);

        rows = dao.query("select * from USERS where username=?", username);
        assertEquals(0, rows.size());
    }

    @Test
    public void testRowIterator() throws Exception {
        DAO dao = getDAO();
        RowIterator rowIterator = dao.queryIterator("select * from USERS");

        try {
            int counter = 0;
            while (rowIterator.next()) {
                Row row = rowIterator.getRow();
                System.out.println(row);
                counter++;
            }

            assertEquals(INITIAL_ROWS_COUNT, counter);
        } finally {
            rowIterator.close();
        }
    }

    @Test
    public void testInsertBlob() throws Exception {
        DAO dao = getDAO();
        dao.execute("insert into LOBTEST(id, BLOB_CONTENT) values(?,?)", 2, "你好".getBytes());
    }

    @Test
    public void testUpdateBlob() throws Exception {
        DAO dao = getDAO();
        dao.execute("update LOBTEST set BLOB_CONTENT=? where ID=?", "111111111111".getBytes(), 1);
    }

    @Test
    public void testInsertBlobAsObject() throws Exception {
        DAO dao = getDAO();
        LobRecord lobRecord = new LobRecord();
        lobRecord.setId(100L);
        lobRecord.setBlobContent("你好".getBytes());
        dao.insert(lobRecord, "LOBTEST");
    }
}

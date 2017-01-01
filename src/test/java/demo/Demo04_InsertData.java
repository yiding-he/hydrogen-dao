package demo;

import com.hyd.dao.DAO;
import com.hyd.dao.SQL;

import java.util.HashMap;
import java.util.Map;

/**
 * (description)
 * created at 2015/3/4
 *
 * @author Yiding
 */
public class Demo04_InsertData extends DemoBase {

    // 本示例介绍如何向表中插入记录
    public static void main(String[] args) {
        DAO dao = getDAO();

        createTable(dao);

        // 方法1：通过执行 insert 语句来插入记录
        dao.execute("insert into users(id,username,password)values(?,?,?)", 1, "admin", "admin");

        // 方法2：通过执行动态拼装的 SQL 来插入记录
        dao.execute(SQL.Insert("users")
                .Values("id", 2)
                .Values("username", "admin")
                .Values("password", "admin"));

        // 方法3：通过保存 Map 对象来插入记录
        Map<String, Object> userMap = new HashMap<String, Object>();
        userMap.put("id", 3);
        userMap.put("username", "admin");
        userMap.put("password", "admin");
        dao.insert(userMap, "users");

        // 方法4：通过保存 Pojo 对象来插入记录
        User user = new User(4, "admin", "admin");
        dao.insert(user, "users");
    }

    public static void createTable(DAO dao) {
        dao.execute("create table users(" +
                "   id int primary key, " +
                "   username varchar(20), " +
                "   password varchar(20)" +
                ")");
    }

    ////////////////////////////////////////////////////////////////

    public static class User {

        private int id;

        private String username;

        private String password;

        public User() {
        }

        public User(int id, String username, String password) {
            this.id = id;
            this.username = username;
            this.password = password;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}

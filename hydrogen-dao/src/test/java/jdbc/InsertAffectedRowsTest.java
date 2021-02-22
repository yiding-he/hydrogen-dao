package jdbc;

import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.Connection;

public class InsertAffectedRowsTest {

    public static void main(String[] args) throws Exception {
        BasicDataSource ds = new BasicDataSource();
        ds.setUrl("jdbc:h2:mem:db1");

        Connection c = ds.getConnection();
        c.createStatement().execute("create table t1(id int)");
        c.createStatement().execute("create table t2(id int)");
        c.createStatement().execute("insert into t1(id) values(1),(2),(3),(4),(5)");

        int count = c.createStatement().executeUpdate("insert into t2 select id from t1");
        System.out.println("count = " + count);
    }
}

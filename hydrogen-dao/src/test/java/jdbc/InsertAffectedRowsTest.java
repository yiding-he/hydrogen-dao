package jdbc;

import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InsertAffectedRowsTest {

    @Test
    public void testInsertAffectedRows() throws Exception {
        try (BasicDataSource ds = new BasicDataSource()) {
            ds.setUrl("jdbc:h2:mem:db1");

            Connection c = ds.getConnection();
            c.createStatement().execute("create table t1(id int)");
            c.createStatement().execute("create table t2(id int)");
            c.createStatement().execute("insert into t1(id) values(1),(2),(3),(4),(5)");

            int count = c.createStatement().executeUpdate("insert into t2 select id from t1");
            assertEquals(5, count);
        }
    }
}

package jdbc;

import com.hyd.dao.database.dialects.Dialects;

import java.sql.DriverManager;

public class ServerNameTest {

    public static void main(String[] args) throws Exception {
        var url = "jdbc:mysql://localhost:2446/global_projects_dev";
        var driverClassName = "com.mysql.cj.jdbc.Driver";
        var username = "global_projects_dev";
        var password = "global_projects_dev";

        Class.forName(driverClassName);
        final var conn = DriverManager.getConnection(url, username, password);
        try (conn) {
            var metaData = conn.getMetaData();
            System.out.println("metaData.getDatabaseProductName() = " + metaData.getDatabaseProductName());
            System.out.println("metaData.getDatabaseProductVersion() = " + metaData.getDatabaseProductVersion());
            System.out.println("metaData.getDatabaseMajorVersion() = " + metaData.getDatabaseMajorVersion());
            System.out.println("metaData.getDatabaseMinorVersion() = " + metaData.getDatabaseMinorVersion());

            var dialect = Dialects.getDialect(conn);
            System.out.println("dialect.getClass() = " + dialect.getClass());
        }
    }
}

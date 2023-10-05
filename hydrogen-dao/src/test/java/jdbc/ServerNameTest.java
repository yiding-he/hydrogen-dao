package jdbc;

import com.hyd.dao.database.dialects.Dialects;
import com.hyd.daotests.DataSourceFactories;

public class ServerNameTest {

    public static void main(String[] args) throws Exception {
        var ds = DataSourceFactories.getDataSource(DataSourceFactories.LOCAL_HOST_TIDB);
        try (var conn = ds.getConnection()) {
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

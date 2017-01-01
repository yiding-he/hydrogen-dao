package demo;


import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;

/**
 * (description)
 * created at 2015/3/4
 *
 * @author Yiding
 */
public class Demo01_CreateDataSource extends DemoBase {

    // 本示例介绍如何创建一个 DataSource。示例使用的是内存数据库 HSQLDB
    // 这个例子与 hydrogen-dao 本身无关，是为了演示如何使用 Apache-DBCP。
    // hydrogen-dao 本身没有实现连接池，它通过管理 DataSource 来管理数据库连接。
    public static void main(String[] args) throws Exception {
        DataSource dataSource = createDataSource();

        // 尝试获取连接
        Connection connection = dataSource.getConnection();
        DatabaseMetaData dbMeta = connection.getMetaData();
        System.out.println("DB name: " + dbMeta.getDatabaseProductName());
        System.out.println("DB version: " + dbMeta.getDatabaseProductVersion());

        connection.close();
    }
}

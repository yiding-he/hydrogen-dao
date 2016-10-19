package com.hyd.daotest.h2;

import com.hyd.dao.database.connection.ConnectionUtil;
import com.hyd.dao.util.ResultSetUtil;
import com.hyd.daotest.BaseTest;
import org.junit.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.HashMap;

/**
 * (description)
 * created at 2015/2/10
 *
 * @author Yiding
 */
public class TestReadingColumns extends BaseTest {

    @Override
    public void setUp() {
        dataSources.getDAO("test")
                .execute("create table users(id int, name varchar(20))");
    }

    @Test
    public void testReadColumns() throws Exception {
        DataSource dataSource = dataSources.getDataSources().get("test");
        // readTables(dataSource);
        readTableColumns(dataSource, "DB1", "PUBLIC", "USERS", "%");
    }

    private void readTables(DataSource dataSource) throws Exception {
        Connection connection = dataSource.getConnection();
        ResultSet tables = connection.getMetaData().getTables(null, null, "%", null);
        System.out.println("Tables: ");
        outputResultSet(tables);
    }

    public static void readTableColumns(DataSource dataSource,
                                  String catalog,
                                  String schemaPattern,
                                  String tableNamePattern,
                                  String columnNamePattern) throws Exception {

        Connection connection = dataSource.getConnection();

        System.out.println("catalog: " + connection.getCatalog());
        System.out.println("db type: " + ConnectionUtil.getDatabaseType(connection));

        ResultSet columns = connection.getMetaData().getColumns(
                catalog, schemaPattern, tableNamePattern, columnNamePattern);

        System.out.println("columns of table '" + tableNamePattern + "':");
        outputResultSet(columns);

        connection.close();
    }

    private static void outputResultSet(ResultSet columns) throws Exception {
        HashMap[] maps = ResultSetUtil.readResultSet(columns);
        for (HashMap map : maps) {
            System.out.println(map);
        }
    }

    @Test
    public void testInsert() throws Exception {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("username", "admin");
        map.put("password", "admin");
        map.put("role", "admin");
        getDAO().insert(map, "T_ABILITY_USER");
    }
}

package com.hyd.daotest.sqlserver;

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
        // 免掉插入数据的步骤
    }

    @Test
    public void testReadColumns() throws Exception {
        DataSource dataSource = dataSources.getDataSources().get("test");
        readCatalogs(dataSource);
        readTableColumns(dataSource, "master", "%", "USERS", "%");
    }

    @Test
    public void testListTables() throws Exception {
        DataSource dataSource = dataSources.getDataSources().get("test");
        Connection connection = dataSource.getConnection();
        ResultSet tables = connection.getMetaData().getTables("exam", "exam", "%", null);
        outputResultset(tables);
        connection.close();
    }

    private void readCatalogs(DataSource dataSource) throws Exception {
        System.out.println("-------- catalogs: ------------");
        Connection connection = dataSource.getConnection();
        outputResultset(connection.getMetaData().getCatalogs());
        connection.close();
        System.out.println("----------------------------");
    }

    public static void readTableColumns(DataSource dataSource,
                                  String catalog,
                                  String schemaPattern,
                                  String tableNamePattern,
                                  String columnNamePattern) throws Exception {

        Connection connection = dataSource.getConnection();

        ResultSet columns = connection.getMetaData().getColumns(
                catalog, schemaPattern, tableNamePattern, columnNamePattern);

        System.out.println("columns of table " + tableNamePattern + ":");
        outputResultset(columns);

        connection.close();
    }

    public static void outputResultset(ResultSet rs) throws Exception {
        HashMap[] maps = ResultSetUtil.readResultSet(rs);
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

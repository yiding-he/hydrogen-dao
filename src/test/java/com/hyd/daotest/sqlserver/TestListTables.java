package com.hyd.daotest.sqlserver;

import com.hyd.dao.DAO;
import com.hyd.dao.DataSources;
import com.hyd.dao.Row;
import org.apache.commons.dbcp.BasicDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.List;

/**
 * (description)
 * created at 2015/6/13
 *
 * @author Yiding
 */
public class TestListTables {

    public static void main(String[] args) throws Exception {
        String url = "jdbc:sqlserver://192.168.1.191:1433;databaseName=netexam";
        String driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
        BasicDataSource ds = new BasicDataSource();
        ds.setUrl(url);
        ds.setDriverClassName(driver);
        ds.setUsername("sa");
        ds.setPassword("xinkao.xgg");

        DataSources dataSources = new DataSources();
        dataSources.setDataSource("default", ds);

        listTables(dataSources);
    }

    private static void queryData(DataSources dataSources) {
        DAO dao = dataSources.getDAO("default");
        List<Row> rows = dao.query("select top 10 * from b_student");
        for (Row row : rows) {
            System.out.println(row);
        }
    }

    private static void listTables(DataSources dataSources) throws Exception {
        DataSource dataSource = dataSources.getDataSources().get("default");
        Connection connection = dataSource.getConnection();
        ResultSet tables = connection.getMetaData().getTables("netexam", "%", "%", new String[]{"TABLE"});
        TestReadingColumns.outputResultset(tables);
        connection.close();
    }
}

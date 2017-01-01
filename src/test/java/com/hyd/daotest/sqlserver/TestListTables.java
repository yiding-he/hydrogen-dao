package com.hyd.daotest.sqlserver;

import com.hyd.dao.DAO;
import com.hyd.dao.DataSources;
import com.hyd.dao.Row;
import com.hyd.dao.util.DBCPDataSource;

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

    // setup a sqlserver database first
    public static void main(String[] args) throws Exception {

        DataSources dataSources = new DataSources();
        dataSources.setDataSource("default",
                DBCPDataSource.newSqlServerDataSource(
                        "localhost", 1433, "netexam", "sa", ""));

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

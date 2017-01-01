package com.hyd.daotest;

import com.hyd.dao.ConnectionExecutor;
import com.hyd.dao.DataSources;
import com.hyd.dao.Row;
import com.hyd.dao.database.ColumnInfo;
import com.hyd.dao.database.commandbuilder.helper.CommandBuilderHelper;
import com.hyd.dao.util.DBCPDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;

/**
 * (description)
 * created at 2015/3/26
 *
 * @author Yiding
 */
public class ReadTableTest {

    public static void main(String[] args) throws Exception {
        DataSource dataSource =
                DBCPDataSource.newOracleDataSource("192.168.102.2", 1521, "hnancrm", "ucr_cen1", "123");

        DataSources dataSources = new DataSources();
        dataSources.setDataSource("db1", dataSource);

        dataSources.withConnection("db1", new ConnectionExecutor() {

            public void execute(Connection connection) throws SQLException {
                CommandBuilderHelper helper = CommandBuilderHelper.getHelper(connection);
                ColumnInfo[] columns = helper.getColumnInfos("%", "TD_MS_WORKER_PROPERTIS");

                for (ColumnInfo column : columns) {
                    System.out.println(column);
                }
            }
        });

        Row row = new Row();
        row.put("PARAM_NAME", "dfadfas");
        row.put("PARAM_VALUE", "dfadfas");
        row.put("WORKER_NAME", "dfadfas");

        dataSources.getDAO("db1").insert(Arrays.asList(row), "TD_MS_WORKER_PROPERTIS");
    }
}

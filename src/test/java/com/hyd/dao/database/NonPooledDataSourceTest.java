package com.hyd.dao.database;

import com.hyd.dao.ConnectionExecutor;
import com.hyd.dao.DataSources;
import com.hyd.daotest.BaseTest;
import org.junit.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class NonPooledDataSourceTest {

    @Test
    public void testGetConnection() throws Exception {
        NonPooledDataSource dataSource = new NonPooledDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost/dao-test?useUnicode=true&amp;characterEncoding=utf8");
        dataSource.setUsername("dao-test");
        dataSource.setPassword("dao-test");

        DataSources dataSources = new DataSources();
        dataSources.setDataSource("db1", dataSource);

        dataSources.withConnection("db1", new ConnectionExecutor() {

            public void execute(Connection connection) throws SQLException {
                ResultSet rs = connection.createStatement().executeQuery("select * from users");
                int columnCount = rs.getMetaData().getColumnCount();

                while (rs.next()) {
                    Map<String, Object> map = new HashMap<String, Object>();
                    for (int i = 1; i <= columnCount; i++) {
                        map.put(rs.getMetaData().getColumnName(i), rs.getObject(i));
                    }
                    System.out.println(map);
                }

                rs.close();
            }
        });
    }
}
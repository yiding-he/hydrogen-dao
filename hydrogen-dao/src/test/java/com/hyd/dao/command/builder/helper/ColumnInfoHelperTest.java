package com.hyd.dao.command.builder.helper;

import com.hyd.dao.DAO;
import com.hyd.dao.DataSources;
import com.hyd.dao.Row;
import com.hyd.dao.database.ColumnInfo;
import com.hyd.dao.database.ConnectionContext;
import com.hyd.dao.database.FQN;
import com.hyd.dao.mate.util.DBCPDataSource;
import com.hyd.dao.mate.util.ResultSetUtil;
import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;

public class ColumnInfoHelperTest {

    @Test
    public void testForMySQL() throws Exception {
        String url = "jdbc:mysql://localhost/?serverTimezone=UTC";
        BasicDataSource dataSource = DBCPDataSource.newMySqlDataSource(url, "root", "root123");
        Connection connection = dataSource.getConnection();
        ConnectionContext context = ConnectionContext.create(connection);

        List<ColumnInfo> columnInfo = ColumnInfoHelper.getColumnInfo(new FQN(context, "demo.blog"), connection);
        columnInfo.forEach(System.out::println);
    }


    @Test
    public void testForH2() throws Exception {
        BasicDataSource dataSource = DBCPDataSource.newH2MemDataSource();
        DataSources dataSources = DataSources.getInstance();
        dataSources.setDataSource("h2", dataSource);

        DAO dao = new DAO("h2");
        List<Row> schemas = dao.query("show schemas");
        schemas.forEach(System.out::println);

        dao.execute("create table table1(id int primary key, name varchar(100))");
        dao.execute("create table \"table2\"(id int primary key, name varchar(100))");
        dao.execute("insert into table1 set id=?, name=?", 0, "Hello, world");
        dao.execute("insert into \"table2\" set id=?, name=?", 0, "Hello, world");

        System.out.println("//////////////////////////////////////////////////////////////");
        dao.query("select * from table1").forEach(System.out::println);
        System.out.println("//////////////////////////////////////////////////////////////");
        dao.query("select * from \"table2\"").forEach(System.out::println);
        System.out.println("//////////////////////////////////////////////////////////////");

        dataSources.withConnection("h2", connection -> {
            try {
                ResultSet columns = connection.getMetaData().getColumns(connection.getCatalog(), "PUBLIC", "TABLE1", "%");
                List<Row> maps = ResultSetUtil.readResultSet(columns);
                if (!maps.isEmpty()) {
                    for (HashMap map : maps) {
                        System.out.println(map);
                    }
                } else {
                    System.err.println("No column found for table1");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        System.out.println("//////////////////////////////////////////////////////////////");

        dataSources.withConnection("h2", connection -> {
            try {
                ConnectionContext context = ConnectionContext.create(connection);
                FQN table1 = new FQN(context, "table1");
                List<ColumnInfo> columnInfos = CommandBuilderHelper.getColumnInfos(table1, context);
                System.out.println("Columns count of table1: " + columnInfos.size());

                for (ColumnInfo columnInfo : columnInfos) {
                    System.out.println(columnInfo);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        System.out.println("//////////////////////////////////////////////////////////////");
    }

}

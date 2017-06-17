package com.hyd.dao.util;

import com.hyd.dao.DAO;
import com.hyd.dao.DataSources;
import org.apache.commons.dbcp2.BasicDataSource;

import java.nio.charset.Charset;

/**
 * @author yidin
 */
public class ScriptExecutorTest {

    public static void main(String[] args) throws Exception {
        BasicDataSource ds = DBCPDataSource.newMySqlDataSource(
                "localhost", 3306, "db1",
                "user_of_db1", "pass_of_db1", true, "UTF-8");

        DataSources dataSources = new DataSources();
        dataSources.setDataSource("db1", ds);

        DAO dao = dataSources.getDAO("db1");
        String filePath = "src/test/resources/test_table_mysql.sql";
        ScriptExecutor.execute(filePath, dao, Charset.forName("UTF-8"));
    }
}
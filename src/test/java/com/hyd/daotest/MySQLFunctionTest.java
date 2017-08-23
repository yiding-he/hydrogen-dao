package com.hyd.daotest;

import com.hyd.dao.DAO;
import com.hyd.dao.DataSources;
import com.hyd.dao.util.DBCPDataSource;
import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.Test;

import java.util.List;

/**
 * (description)
 * created at 2017/7/29
 *
 * @author yidin
 */
public class MySQLFunctionTest {

    static {
        System.setProperty("socksProxyHost", "127.0.0.1");
        System.setProperty("socksProxyPort", "2346");
    }

    @Test
    public void testCallFunction() throws Exception {
        BasicDataSource ds = DBCPDataSource.newMySqlDataSource(
                "10.10.22.145", 3306, "knowledge_contest_activity", "root", "znxz", true, "UTF-8");
        DataSources dataSources = new DataSources();
        dataSources.setDataSource("default", ds);

        DAO dao = dataSources.getDAO("default");
        List result = dao.call("rank");
        System.out.println(result);
    }
}

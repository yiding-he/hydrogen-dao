package com.hyd.daotest;

import com.hyd.dao.DAO;
import com.hyd.dao.DataSources;
import com.hyd.dao.Row;
import com.hyd.dao.SQL;
import org.apache.commons.dbcp.BasicDataSource;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * todo: description
 *
 * @author yiding.he
 */
public class Demo {

    private static DataSources createDataSourceManager() {
        DataSources dataSourceManager = new DataSources();
        HashMap<String, DataSource> dataSources = new HashMap<String, DataSource>();
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setUrl("jdbc:oracle:thin:@124.232.138.73:1521:hdstdb");
        dataSource.setDriverClassName("oracle.jdbc.OracleDriver");
        dataSource.setUsername("wx_OA");
        dataSource.setPassword("xfire_wx_OA_2013");
        dataSources.put("db1", dataSource);
        dataSourceManager.setDataSources(dataSources);
        return dataSourceManager;
    }

    /**
     * insert into t (a,v,c,d,e) values(#{aa}, #{aa}, #{aa}, #{aa}, #{aa}, )
     *
     * @param args
     */
    public static void main(String[] args) {
        DataSources dataSources = createDataSourceManager();

        DAO dao = dataSources.getDAO("db1");

        List<String> emails = Arrays.asList("1849238652@qq.com", "1421450400@qq.com", "14060932@qq.com", "540526543@qq.com");
        String username = null;
        List<Row> rows = dao.query(
                SQL.Select("*").From("t_wx_admin")
                        .Where("email in ?", emails)
                        .And(username != null, "user_name=?", username)
        );

        dao.execute(SQL.Insert("t_wx_admin").Values("id", 1).Values("name", "2"));

        dao.execute("delete ? and ?", "1", "2");

        Long next = dao.next("seq1");

        // in out
        List nn = dao.call("nn", 1, 2, 3, 4);
    }
}

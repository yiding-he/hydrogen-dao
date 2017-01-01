package com.hyd.daotest.mysql;

import com.hyd.dao.DataSources;
import org.apache.commons.dbcp2.BasicDataSource;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * todo: description
 *
 * @author yiding.he
 */
public class Init {

    private static Map<String, Info> infos = new HashMap<String, Info>();

    static {
        infos.put("kdian", new Info("kdian001.mysql.rds.aliyuncs.com", 3860, "kdian", "ySxwFPWJyEVLLdSE"));
        infos.put("localhost", new Info("localhost", 3306, "test", ""));
    }

    public static DataSources initDataSource(String name) {
        Info info = infos.get(name);
        BasicDataSource basicDataSource = initBasicDataSource(info.host, info.port, info.username, info.password, info.database);

        DataSources dsManager = new DataSources();
        dsManager.setDataSources(new HashMap<String, DataSource>());
        dsManager.getDataSources().put("0", basicDataSource);
        return dsManager;
    }

    private static BasicDataSource initBasicDataSource(final String host, final int port, String username, String password, final String database) {
        BasicDataSource basicDataSource = new BasicDataSource();
        basicDataSource.setDriverClassName("com.mysql.jdbc.Driver");
        basicDataSource.setUrl("jdbc:mysql://" + host + ":" + port + "/" + database + "?characterEncoding=utf8");
        basicDataSource.setUsername(username);
        basicDataSource.setInitialSize(1);
        basicDataSource.setPassword(password);
        return basicDataSource;
    }

    /////////////////////////////////////////////////////////

    private static class Info {

        public String host;

        public int port;

        public String username;

        public String password;

        public String database;

        private Info(String host, int port, String username, String password) {
            this.host = host;
            this.port = port;
            this.username = username;
            this.password = password;
            this.database = username;
        }
    }
}

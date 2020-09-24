package com.hyd.dao;

import com.hyd.dao.database.JDBCDriver;
import com.hyd.dao.database.NonPooledDataSource;

import javax.sql.DataSource;

/**
 * @author yiding.he
 */
public class DAOUtils {

    private static final DataSources dataSources = new DataSources();

    public static DAO getDAO() {
        if (dataSources.isEmpty()) {
            dataSources.setDataSource(DataSources.DEFAULT_DATA_SOURCE_NAME, createDataSource());
        }

        return dataSources.getDAO(DataSources.DEFAULT_DATA_SOURCE_NAME);
    }

    private static DataSource createDataSource() {
        String url = System.getProperty("jdbc.url");
        JDBCDriver driver = JDBCDriver.getDriverByUrl(url);

        if (driver == null) {
            throw new DAOException("Driver not found for " + url);
        }

        return new NonPooledDataSource(
                driver.getAvailableDriver().getCanonicalName(),
                url,
                System.getProperty("jdbc.username"),
                System.getProperty("jdbc.password")
        );
    }

    public static void setupDataSource(String url, String username, String password) {
        System.setProperty("jdbc.url", url);
        if (username != null) {
            System.setProperty("jdbc.username", username);
        }
        if (password != null) {
            System.setProperty("jdbc.password", password);
        }
    }

    public static void setupLocalMySQL() {
        setupDataSource("jdbc:mysql://localhost/test", "root", "root123");
    }
}

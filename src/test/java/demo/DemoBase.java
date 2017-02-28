package demo;

import com.hyd.dao.DAO;
import com.hyd.dao.DataSources;
import org.apache.commons.dbcp2.BasicDataSource;

import javax.sql.DataSource;

/**
 * @author yiding.he
 */
public class DemoBase {

    protected static DataSources dataSources;

    protected static DataSource createDataSource() {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName("org.hsqldb.jdbc.JDBCDriver");
        dataSource.setUrl("jdbc:hsqldb:mem:demodb");
        dataSource.setUsername("SA");
        return dataSource;
    }


    public static DAO getDAO() {
        DataSource dataSource = createDataSource();

        dataSources = new DataSources();
        dataSources.setDataSource("demodb1", dataSource);
        return dataSources.getDAO("demodb1");
    }

}

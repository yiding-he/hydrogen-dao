package demo;

import com.hyd.dao.DAO;
import com.hyd.dao.DataSources;

import javax.sql.DataSource;

/**
 * (description)
 * created at 2015/3/4
 *
 * @author Yiding
 */
public class Demo02_CreateDAO extends DemoBase {

    // 本例子介绍如何创建 hydrogen-dao 的主要对象：DAO
    public static void main(String[] args) {

        // 1. 创建一个 DataSource 对象
        DataSource dataSource = createDataSource();

        // 2. 将 DataSource 对象注册到 com.hyd.dao.DataSources 对象中
        // 这两步通常会在 Spring 当中以配置的方式完成。
        // DataSources 对象应该是全局唯一的。
        DataSources dataSources = new DataSources();
        dataSources.setDataSource("demodb1", dataSource);

        // 3. 获取 DAO 对象。
        DAO dao = dataSources.getDAO("demodb1");
    }
}

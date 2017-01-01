package demo;

import com.hyd.dao.DAO;
import com.hyd.dao.DAOException;
import com.hyd.daotest.hsqldb.TestReadingColumns;

import javax.sql.DataSource;

/**
 * (description)
 * created at 2015/3/4
 *
 * @author Yiding
 */
public class Demo03_CreateTable extends DemoBase {

    public static void main(String[] args) {
        DAO dao = getDAO();

        try {

            // execute() 方法用于执行任意 SQL 语句。这里
            // 执行一条 create table 语句
            dao.execute("create table users(" +
                    "   id int primary key, " +
                    "   username varchar(20), " +
                    "   password varchar(20)" +
                    ")");
            System.out.println("表已创建。");

        } catch (DAOException e) {   // DAO 的大部分方法都抛出 DAOException 类型的异常

            System.err.println("表创建失败。");
            e.printStackTrace();
        }

        readColumns();
    }

    private static void readColumns() {
        try {
            DataSource dataSource = dataSources.getDataSources().get("demodb1");
            TestReadingColumns.readTableColumns(dataSource, "PUBLIC", null, "USERS", "%");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

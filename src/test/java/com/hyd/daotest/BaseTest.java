package com.hyd.daotest;

import com.hyd.dao.DAO;
import com.hyd.dao.DataSources;
import com.hyd.dao.database.connection.ConnectionUtil;
import com.hyd.dao.util.DBCPDataSource;
import org.apache.commons.dbcp.BasicDataSource;
import org.h2.jdbcx.JdbcConnectionPool;
import org.junit.Before;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author yiding.he
 */
public abstract class BaseTest {

    public static String DB_TYPE = "mysql";   // oracle, mysql, hsqldb, sqlserver, h2

    protected DataSources dataSources = new DataSources();

    protected DataSource createOracleDataSource() {
        return DBCPDataSource.newOracleDataSource(
                "192.168.1.200", 1521, "xfireorc", "DAOTEST", "DAOTEST");
    }

    protected DataSource createMySQLDataSource() {
        return DBCPDataSource.newMySqlDataSource(
                "localhost", 3306, "dao-test", "dao-test", "dao-test", true, "utf8");
    }

    protected DataSource createHSQLDBTestDataSource() {
        return DBCPDataSource.newRemoteHsqldbDataSource(
                "localhost", 9001, "xdb", "SA", null);
    }

    protected DataSource createSQLServerTestDataSource() {
        String url = "jdbc:sqlserver://localhost:1433;databaseName=exam";
        String driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
        BasicDataSource ds = new BasicDataSource();
        ds.setUrl(url);
        ds.setDriverClassName(driver);
        ds.setUsername("exam");
        ds.setPassword("exam");
        return ds;
    }

    protected DataSource createH2DataSource() {
        return JdbcConnectionPool.create("jdbc:h2:mem:db1", "sa", "");
    }

    ////////////////////////////////////////////////////////////////////////////////

    {
        DataSource ds;
        if (DB_TYPE.equals("oracle")) {
            ds = createOracleDataSource();
        } else if (DB_TYPE.equals("mysql")) {
            ds = createMySQLDataSource();
        } else if (DB_TYPE.equals("hsqldb")) {
            ds = createHSQLDBTestDataSource();
        } else if (DB_TYPE.equals("sqlserver")) {
            ds = createSQLServerTestDataSource();
        } else if (DB_TYPE.equals("h2")) {
            ds = createH2DataSource();
        } else {
            throw new IllegalStateException("Unknown DB_TYPE '" + DB_TYPE + "'");
        }

        try {
            Connection connection = ds.getConnection();
            System.out.println("本次测试使用的数据库：" + ConnectionUtil.getDatabaseType(connection));
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        dataSources.getDataSources().put("test", ds);
        dataSources.getDataSources().put("test2", ds);
    }

    protected boolean isSequenceEnabled() {
        return "oracle".equals(DB_TYPE);
    }

    protected boolean isAutoIncrementEnabled() {
        return "mysql".equals(DB_TYPE) || "hsqldb".equals(DB_TYPE) || "sqlserver".equals(DB_TYPE);
    }

    protected boolean isLobSupported() {
        return "oracle".equals(DB_TYPE) || "sqlserver".equals(DB_TYPE);
    }

    protected DAO getDAO() {
        return dataSources.getDAO("test");
    }

    protected DAO getDAO2() {
        return dataSources.getDAO("test2");
    }

    /////////////////////////////////////////////////////////

    public static final int INITIAL_ROWS_COUNT = 20;

    @Before
    public void setUp() {
        DAO dao = getDAO();

        ////////////////////////////////////////////////////////////////
        dao.execute("delete from USERS");

        AtomicInteger id = new AtomicInteger(0);
        for (int i = 1; i < INITIAL_ROWS_COUNT + 1; i++) {
            String username = String.format("user%03d", i);

            if (isSequenceEnabled()) {
                dao.execute("insert into USERS(ID, USERNAME, PASSWORD, ROLE_ID) " +
                        "values (SEQ_USER_ID.nextval,?,?,?)", username, "pass" + i, 1);
            } else if (isAutoIncrementEnabled()) {
                dao.execute("insert into USERS(USERNAME, PASSWORD, ROLE_ID) " +
                        "values (?,?,?)", username, "pass" + i, 1);
            } else {
                dao.execute("insert into USERS(ID, USERNAME, PASSWORD, ROLE_ID) " +
                        "values (?, ?,?,?)", id.incrementAndGet(), username, "pass" + i, 1);
            }
        }

        ////////////////////////////////////////////////////////////////
        dao.execute("delete from ROLES");
        if (isAutoIncrementEnabled()) {
            dao.execute("insert into ROLES(NAME) values(?)", "admin");
        } else {
            dao.execute("insert into ROLES(ID, NAME) values(?, ?)", 1, "admin");
        }

        ////////////////////////////////////////////////////////////////

        if (isLobSupported()) {
            dao.execute("delete from LOBTEST");

            if (isAutoIncrementEnabled()) {
                if ("sqlserver".equals(DB_TYPE)) {
                    dao.execute("insert into LOBTEST(blob_content, clob_content) values(" +
                            " convert(varbinary(max),'111111你好'), '222222你好'" +
                            ")");
                } else {
                    dao.execute("insert into LOBTEST(blob_content, clob_content) values('111111你好', '222222你好')");
                }
            } else {
                dao.execute("insert into LOBTEST(id, blob_content, clob_content) values(1, empty_blob(), empty_clob())");
            }
        }
    }

}

package com.hyd.daotest;

import com.hyd.dao.DAO;
import com.hyd.dao.DataSources;
import org.apache.commons.dbcp.BasicDataSource;
import org.junit.Before;

/**
 * @author yiding.he
 */
public abstract class BaseTest {

    public static String DB_TYPE = "mysql";   // oracle, mysql, hsqldb

    protected DataSources dataSources = new DataSources();

    protected BasicDataSource createOracleDataSource() {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName("oracle.jdbc.OracleDriver");
        dataSource.setUrl("jdbc:oracle:thin:@192.168.1.200:1521:xfireorc");
        dataSource.setUsername("DAOTEST");
        dataSource.setPassword("DAOTEST");
        return dataSource;
    }

    protected BasicDataSource createMySQLDataSource() {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost/dao-test?useUnicode=true&amp;characterEncoding=utf8");
        dataSource.setUsername("dao-test");
        dataSource.setPassword("dao-test");
        return dataSource;
    }

    protected BasicDataSource createHSQLDBTestDataSource() {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName("org.hsqldb.jdbc.JDBCDriver");
        dataSource.setUrl("jdbc:hsqldb:hsql://localhost/xdb");
        dataSource.setUsername("SA");
        return dataSource;
    }

    {
        BasicDataSource ds;
        if (DB_TYPE.equals("oracle")) {
            ds = createOracleDataSource();
        } else if (DB_TYPE.equals("mysql")) {
            ds = createMySQLDataSource();
        } else if (DB_TYPE.equals("hsqldb")) {
            ds = createHSQLDBTestDataSource();
        } else {
            throw new IllegalStateException("Unknown DB_TYPE '" + DB_TYPE + "'");
        }

        dataSources.getDataSources().put("test", ds);
        dataSources.getDataSources().put("test2", ds);
    }

    protected boolean isOracle() {
        return "oracle".equals(DB_TYPE);
    }

    protected DAO getDAO() {
        return dataSources.getDAO("test");
    }

    protected DAO getDAO2() {
        return dataSources.getDAO("test2");
    }

    /////////////////////////////////////////////////////////

    public static final int INITIAL_ROWS_COUNT = 10;

    @Before
    public void setUp() {
        DAO dao = getDAO();

        ////////////////////////////////////////////////////////////////
        dao.execute("delete from USERS");

        for (int i = 1; i < INITIAL_ROWS_COUNT + 1; i++) {
            if (isOracle()) {
                dao.execute("insert into USERS(ID, USERNAME, PASSWORD, ROLE_ID) " +
                        "values (SEQ_USER_ID.nextval,?,?,?)", "user" + i, "pass" + i, 1);
            } else {
                dao.execute("insert into USERS(USERNAME, PASSWORD, ROLE_ID) " +
                        "values (?,?,?)", "user" + i, "pass" + i, 1);
            }
        }

        ////////////////////////////////////////////////////////////////
        dao.execute("delete from ROLES");
        dao.execute("insert into ROLES(ID, NAME) values(?, ?)", 1, "admin");

        ////////////////////////////////////////////////////////////////
        dao.execute("delete from LOBTEST");

        if (isOracle()) {
            dao.execute("insert into LOBTEST(id, blob_content, clob_content) values(1, empty_blob(), empty_clob())");
        }
    }

}

package com.hyd.dao;

import com.hyd.dao.util.CSVReader;
import com.hyd.dao.util.DBCPDataSource;
import com.hyd.dao.util.ScriptExecutor;
import org.junit.BeforeClass;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;

/**
 * @author yiding_he
 */
public class BaseTest {

    static final DataSources DATA_SOURCES = new DataSources();

    static DAO getDAO() {
        return DATA_SOURCES.getDAO("h2");
    }

    @BeforeClass
    public static void initMemDB() throws Exception{
        DATA_SOURCES.setDataSource("h2", DBCPDataSource.newH2MemDataSource());

        Charset charset = Charset.forName("UTF-8");
        DAO dao = DATA_SOURCES.getDAO("h2");

        ScriptExecutor.execute(resource("/scripts/tables.sql"), dao, charset);

        List<Row> blogRows = CSVReader.read(resource("/scripts/data.csv"), charset);
        dao.insert(blogRows, "blog");
    }

    private static InputStream resource(String s) {
        return BaseTest.class.getResourceAsStream(s);
    }
}

package com.hyd.daotests.benchmark;

import com.hyd.dao.DAO;
import com.hyd.dao.DataSources;
import com.hyd.dao.mate.util.DBCPDataSource;
import com.hyd.dao.repository.Repository;
import lombok.Data;
import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static com.hyd.dao.DataSources.DEFAULT_DATA_SOURCE_NAME;

// 性能测试的目的是衡量代码效率，所以只测试单核性能
public class InsertBenchmark {

    public static final String DROP_TABLE = "drop table if exists table1";

    public static final String CREATE_TABLE = "create table table1(" +
        "num_value1 int, " +
        "num_value2 int, " +
        "num_value3 int, " +
        "str_value1 varchar(100), " +
        "str_value2 varchar(100), " +
        "str_value3 varchar(100), " +
        "date_value1 timestamp, " +
        "date_value2 timestamp, " +
        "date_value3 timestamp" +
        ")";

    public static final String INSERT = "insert into table1" +
        "(num_value1,num_value2,num_value3,str_value1,str_value2,str_value3,date_value1,date_value2,date_value3)" +
        "values(?,?,?,?,?,?,?,?,?)";

    @Data
    public static class Bean {

        private Long numValue1;

        private Long numValue2;

        private Long numValue3;

        private String strValue1;

        private String strValue2;

        private String strValue3;

        private Date dateValue1;

        private Date dateValue2;

        private Date dateValue3;
    }

    private DAO dao;

    {
        BasicDataSource dataSource = DBCPDataSource.newH2MemDataSource();
        DataSources.getInstance().setDataSource(DEFAULT_DATA_SOURCE_NAME, dataSource);
        this.dao = new DAO(DEFAULT_DATA_SOURCE_NAME);
    }

    @Before
    public void init() {
        this.dao.execute(DROP_TABLE);
        this.dao.execute(CREATE_TABLE);
    }

    //////////////////////////////////////////////////////////////

    @Test
    public void testInsertBySQL() throws Exception {
        Date now = new Date();
        for (int i = 0; i < 100000; i++) {
            this.dao.execute(INSERT, 1, 2, 3, "1", "2", "3", now, now, now);
        }
    }

    @Test
    public void testInsertByBean() throws Exception {
        Date now = new Date();
        Bean bean = new Bean();
        bean.setNumValue1(1L);
        bean.setNumValue2(2L);
        bean.setNumValue3(3L);
        bean.setStrValue1("1");
        bean.setStrValue2("2");
        bean.setStrValue3("3");
        bean.setDateValue1(now);
        bean.setDateValue2(now);
        bean.setDateValue3(now);

        Repository<Bean> beanRepository = new Repository<>(Bean.class, dao, "table1");
        for (int i = 0; i < 100000; i++) {
            beanRepository.insertInstance(bean);
        }
    }
}

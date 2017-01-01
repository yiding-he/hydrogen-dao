package com.hyd.daotest.mysql;

import com.hyd.dao.DAO;
import com.hyd.dao.DataSources;
import com.hyd.dao.Row;
import com.hyd.daotest.bean.Order;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.junit.Test;

import java.util.List;

/**
 * todo: description
 *
 * @author yiding.he
 */
public class TestSelect {

    private DataSources dsManager = Init.initDataSource("kdian");

    @Test
    public void testSelect1() throws Exception {

        DAO dao = dsManager.getDAO("0");
        List<Row> rowList = dao.query("select * from t_vendor");

        for (Row row : rowList) {
            System.out.println(row);
        }
    }

    @Test
    public void testExtraField() throws Exception {
        DAO dao = dsManager.getDAO("0");
        List<Order> query = dao.query(Order.class,
                "select o.*, v.NAME as VENDOR_NAME from T_ORDER o, T_VENDOR v " +
                        "where CUSTOMER_ID=? and o.VENDOR_ID=v.ID order by o.ID desc", 2
        );

        for (Order order : query) {
            System.out.println(ReflectionToStringBuilder.toString(order));
        }
    }
}

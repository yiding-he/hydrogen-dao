package com.hyd.dao;

import com.hyd.dao.command.Command;
import org.junit.Test;

public class SQLTest {

    @Test
    public void testJoin() throws Exception {
        SQL.Select select = SQL.Select("*")
            .From("table1 t1")
            .InnerJoin("Table2 t2 on t1.id=t2.id+?", 10)
            .Where("t2.name=?", "aaa");

        Command command = select.toCommand();
        System.out.println(command.getStatement());
        System.out.println(command.getParams());
    }
}

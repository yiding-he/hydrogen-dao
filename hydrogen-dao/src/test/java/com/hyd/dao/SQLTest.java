package com.hyd.dao;

import com.hyd.dao.command.Command;
import org.junit.Test;

import java.util.Arrays;

public class SQLTest {

    private static void output(SQL.Generatable<?> generatable) {
        Command command = generatable.toCommand();
        System.out.println(command.getStatement());
        System.out.println(command.getParams());
    }

    @Test
    public void testJoin() throws Exception {
        String name = null;
        SQL.Select select = SQL.Select("*")
            .From("table1 t1")
            .InnerJoin("Table2 t2 on t1.id=t2.id+?", 10)
            .IfNotEmpty(name, (_select, _name) ->
                _select.Where("t2.name=?", _name.toUpperCase()));
        output(select);
    }

    @Test
    public void testJoin2() throws Exception {
        SQL.Select select = SQL.Select("*")
            .From("table1 t1")
            .InnerJoin("Table2 t2 on t1.id=t2.id+?", 10)
            .Where("t2.name=?", "aaa")
            .Where("t2.name=?", "bbb")
            .Where("t2.name=?", "ccc")
            ;

        output(select);
    }

    @Test
    public void testIn() throws Exception {
        SQL.Select select = SQL.Select("*")
            .From("table1 t1")
            .Where("name in ?", Arrays.asList("name1", "name2", "name3"))
            .Or("id in ?", "id1", "id2", "id3")
            ;

        output(select);
    }
}

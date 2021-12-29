package com.hyd.dao;

import com.hyd.dao.command.Command;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static com.hyd.dao.SQL.Select;

public class SQLTest {

    private static void output(SQL.Generatable<?> generatable) {
        Command command = generatable.toCommand();
        System.out.println(command.getStatement());
        System.out.println(command.getParams());
    }

    @Test
    public void testJoin() throws Exception {
        String name = null;
        Select select = Select("*")
            .From("table1 t1")
            .InnerJoin("Table2 t2 on t1.id=t2.id+?", 10)
            .IfNotEmpty(name, (_select, _name) ->
                _select.Where("t2.name=?", _name.toUpperCase()));
        output(select);
    }

    @Test
    public void testJoin2() throws Exception {
        Select select = Select("*")
            .From("table1 t1")
            .InnerJoin("Table2 t2 on t1.id=t2.id+?", 10)
            .Where("t2.name=?", "aaa")
            .Where("t2.name=?", "bbb")
            .Where("t2.name=?", "ccc");

        output(select);
    }

    @Test
    public void testIn() throws Exception {
        Select select = Select("*")
            .From("table1 t1")
            .Where("name in ?", Arrays.asList("name1", "name2", "name3"))
            .Or("id in ?", "id1", "id2", "id3");

        output(select);
    }

    @Test
    public void testChildStatements() throws Exception {
        output(Select("*").From("t1")
            .Where("col1 in", Select("pid").From("t2").Where("t2.name=?", "aaa"))
            .And("col2 not in ", Select("qid").From("t3").Where("t3.xxx in ?", "111", "222", "333"))
            .OrderBy("col3 desc")
            .Limit(100)
        );
    }
}

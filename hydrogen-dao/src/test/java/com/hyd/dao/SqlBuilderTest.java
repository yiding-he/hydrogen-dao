package com.hyd.dao;

import org.junit.Test;

public class SqlBuilderTest {

    @Test
    public void testBuildSql() throws Exception {
        SqlBuilder sqlBuilder = new SqlBuilder() {{
            select("a.id", "b.name", "c.type")
                .from("table_a a", "table_b b", "table_c c")
                .unionAll()
                .select("d.id", "e.name", "f.type")
                .from("table_d d", "table_e e", "table_f f");
        }};

        System.out.println(sqlBuilder.toCommand().getStatement());
        System.out.println(sqlBuilder.toCommand().getParams());
    }
}

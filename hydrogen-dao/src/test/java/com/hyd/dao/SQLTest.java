package com.hyd.dao;

import com.hyd.dao.command.Command;
import com.hyd.dao.database.type.NameConverter;
import com.hyd.daotests.model.Blog;
import org.junit.jupiter.api.Test;

import static com.hyd.dao.SQL.Select;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SQLTest {

    record SqlParam(int id, String disabled, String roleName) {

    }

    private static void output(SQL.Generatable<?> generatable) {
        Command command = generatable.toCommand();
        System.out.println(command.getStatement());
        System.out.println(command.getParams());
    }

    @Test
    public void testCTEQuery() {
        var param = new SqlParam(0, "false", null);
        var sql = SQL
            .With(
                Select("id as role_id", "name as role_name")
                    .From("sys_role")
                    .AsCTE("r")
            )
            .Select("u.id", "u.name as user_name", "r.role_name")
            .From("sys_user u")
            .LeftJoin("r on u.role_id=r.role_id")
            .Where("u.id = ?", param.id())
            .AndIfNotEmpty("u.disabled = ?", param.disabled())
            .AndIfNotEmpty("r.role_name = ?", param.roleName())  // 因为参数为空，生成的 SQL 中不会有这个条件
            ;

        var command = sql.toCommand();
        assertEquals("WITH SELECT id as role_id,name as role_name FROM sys_role AS r " +
                "SELECT u.id,u.name as user_name,r.role_name " +
                "FROM sys_user u " +
                "LEFT JOIN r on u.role_id=r.role_id " +
                "WHERE u.id = ? " +
                "AND u.disabled = ?",
            command.getStatement());
    }

    @Test
    public void testCTEUpdate() {
        var param = new SqlParam(0, "false", null);
        var sql = SQL
            .With(
                Select("id as role_id", "name as role_name")
                    .From("sys_role")
                    .AsCTE("r")
            )
            .Update("sys_user u")
            .Set("u.role_id = r.role_id")
            .Where("u.id = ?", param.id())
            .AndIfNotEmpty("u.disabled = ?", param.disabled())
            .AndIfNotEmpty("r.role_name =?", param.roleName())  // 因为参数为空，生成的 SQL
            ;
        var command = sql.toCommand();
        assertEquals("WITH SELECT id as role_id,name as role_name FROM sys_role AS r " +
                "UPDATE sys_user u " +
                "SET u.role_id = r.role_id " +
                "WHERE u.id = ? " +
                "AND u.disabled = ?",
            command.getStatement());
    }

    @Test
    public void testCTEDelete() {
        var param = new SqlParam(0, "false", null);
        var sql = SQL
            .With(
                Select("id as role_id", "name as role_name")
                    .From("sys_role")
                    .AsCTE("r")
            )
            .Delete("sys_user u")
            .Where("u.id = ?", param.id())
            .AndIfNotEmpty("u.disabled = ?", param.disabled())
            .AndIfNotEmpty("r.role_name =?", param.roleName())  // 因为参数为空，生成的 SQL
           ;
        var command = sql.toCommand();
        assertEquals("WITH SELECT id as role_id,name as role_name FROM sys_role AS r " +
                "DELETE FROM sys_user u " +
                "WHERE u.id = ? " +
                "AND u.disabled = ?",
            command.getStatement());
    }


    @Test
    public void testSubQuery() throws Exception {
        output(Select("*")
            .From("t1")
            .Where("col1 in",
                Select("pid").From("t2").Where("t2.name=?", "aaa"))
            .And("col2 not in ",
                Select("qid").From("t3").Where("t3.xxx in ?", "111", "222", "333"))
            .OrderBy("col3 desc")
            .Limit(100)
        );
    }

    @Test
    public void testSelectFromGetters() {
        output(
            Select(NameConverter.NONE)
                .Columns(Blog::getId, Blog::getCreateTime, Blog::getTitle)
                .From("blog").Limit(10));
    }
}

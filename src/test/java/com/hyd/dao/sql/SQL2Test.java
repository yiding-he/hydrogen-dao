package com.hyd.dao.sql;

import org.junit.Test;

import static com.hyd.dao.sql.SQL2.*;

public class SQL2Test {

    @Test
    public void testBuild() throws Exception {

        /*
         * select
         *   users.user_id, users.first_name, users.last_name,
         *   role.role_id, role.role_name,
         * from
         *   users, role,
         *   ( select login_user_id, last_login from login_log ) as login
         * where
         *   user.role_id = role.role_id and
         *   login.login_user_id = users.user_id and
         *   login.login_time between '2019-01-01' and '2019-12-31' and
         *   users.first_name like '%John' and
         *   users.user_id = (
         *     select user_id from latest_login where login_date='2019-01-31'
         *   ) and
         *   (
         *     users.role_id = 'role1' or
         *     users.role_id = 'role2'
         *   )
         */
        Select(
                Column("user_id", "first_name", "last_name").From("users"),
                Column("role_id", "role_name").From("role"),
                Select(
                        Column("login_user_id").From("login_log"),
                        Column("last_login").From("login_log")
                ).As("login")
        ).WithJoining(
                LeftJoin("users", "role").Using("role_id"),
                LeftJoin("users", "login").On(
                        Column("user_id").From("users")
                                .Equals(Column("login_user_id").From("login"))
                )
        ).AllMatch(
                Column("login_time").From("login").Between("2019-01-01", "2019-12-31"),
                Column("first_name").From("users").Like("%John"),
                Column("user_id").Equals(
                        Select(
                                Column("user_id").From("latest_login")
                        ).AllMatch(
                                Column("login_date").Equals("2019-01-31")
                        )
                ),
                AnyMatch(
                        Column("role_id").From("users").Equals("role1"),
                        Column("role_id").From("users").Equals("role2")
                )
        );

    }

}
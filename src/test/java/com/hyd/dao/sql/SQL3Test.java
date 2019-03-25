package com.hyd.dao.sql;

import org.junit.Test;

import static com.hyd.dao.sql.SQL3.*;

public class SQL3Test {

    /*
     * select
     *   user.user_id, user.first_name, user.last_name,
     *   role.role_id,
     *   login.last_login_time
     * from user
     *   left join role
     *   left join login
     *   on
     *     user.role_id = role.role_id and
     *     user.user_id = login.login_user_id
     * where
     *   role.role_id in ('admin', 'user') and
     *   login.last_login_time <= '2019-01-01'
     */
    public void sql() throws Exception {

        Table user = Table("user");
        Table role = Table("role");
        Table login = Table("login");

        Joining user_role =
                user.LeftJoin(role).Using("role_id");


        Joining user_login =
                user.LeftJoin(login).OnMatch(
                        user.Column("user_id"),
                        login.Column("login_user_id")
                );

        Column[] selections = {
                user.Column("user_id", "first_name", "last_name"),
                role.Column("role_id"),
                login.Column("last_login_time")
        };

        Select(selections)
                .WithJoining(user_role, user_login)
                .WithAllMatch(
                        role.Column("role_id").In("admin", "user"),
                        login.Column("last_login_time").LessOrEqual("2019-01-01")
                )
        ;

    }
}
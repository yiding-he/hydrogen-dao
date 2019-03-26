package com.hyd.dao.sql;

import com.hyd.dao.database.commandbuilder.Command;

import java.util.Date;

public class SQL3Test {

    public static void main(String[] args) throws Exception {
        new SQL3Test().test1();
    }

    /*
     * select
     *   'Hello' as greetings,
     *   user.user_id, user.first_name, user.last_name,
     *   role.role_id, login.last_login_time
     * from user
     *   left join role
     *   left join login
     *   on
     *     user.role_id = role.role_id and
     *     user.user_id = login.login_user_id
     * where
     *   role.role_id in ('admin', 'user') and
     *   user.user_id <= all(
     *     select login_user_id from login
     *     where last_login_time <= '2019-01-01'
     *   ) and
     *   login.last_login_time <= '2019-01-01'
     */
    public void test1() throws Exception {
        new SQL3() {{
            Table user = Table("user");
            Table role = Table("role");
            Table login = Table("login");

            Column login_user_id = login.Column("login_user_id");
            Column user_id = user.Column("user_id");
            Column last_login_time = login.Column("last_login_time");
            Column role_id = role.Column("role_id");

            Joining user_role = user.LeftJoin(role).Using("role_id");
            Joining user_login = user.LeftJoin(login).On(user_id.Equals(login_user_id));

            Selectable[] selections = {
                    Expr("'Hello' as greetings"),
                    user.Column("user_id", "first_name", "last_name"),
                    role_id, last_login_time
            };

            Condition roleCondition = role_id.In("admin", "user");
            Condition loginTimeCondition = last_login_time.LessOrEqual("2019-01-01");

            Condition userIdCondition = user_id.LessOrEqual(
                    All(login_user_id).Match(
                            last_login_time.LessOrEqual("2019-01-01")));

            Select select = Select(selections)
                    .Joins(user_role, user_login)
                    .MatchAll(roleCondition, userIdCondition, loginTimeCondition);

            Command command = select.toCommand();
            System.out.println(command.getStatement());
        }};
    }

    public void test2() {

        // select * from user where
        // user_id <= 1000 and role_id in ('role1', 'role2')

        final int maxUserId = 1000;
        final String[] roles = {"role1", "role2"};

        new SQL3() {{
            Table user = Table("user");
            Select(user.Column("*")).MatchAll(
                    user.Column("user_id").LessOrEqual(maxUserId),
                    user.Column("role_id").In(roles)
            );
        }};

        new SQL3() {
            Table user;

            {
                Select(user.Column("*")).MatchAll(
                        user.Column("user_id").LessOrEqual(maxUserId),
                        user.Column("role_id").In(roles)
                );
            }
        };

        new SQL3() {{
            Select("user.*").MatchAll(
                    Column("user.user_id").LessOrEqual(maxUserId),
                    Column("user.role_id").In(roles)
            );
        }};

        new SQL3() {{
            Select("user.*").MatchAll(
                    Where("user.user_id <= ?", maxUserId),
                    Where("user.role_id in ?", roles),
                    MatchAny(
                            Where("user.login_time > ?", new Date()),
                            Where("user.login_time < ?", new Date())
                    )
            );
        }};

    }

    public void test3() {

        new SQL3() {{
            this
                    .Select("user.*, role.name, login.time")
                    .Joins(
                            LeftJoin("user").With("role").Using("role_id"),
                            LeftJoin("user").With("login")
                                    .On("user.user_id = login.login_user_id")
                    )
                    .MatchAll(
                            Where("user.user_id <= ?", 1000),
                            Where("user.role_id in ?", "admin")
                    );
        }};

    }
}
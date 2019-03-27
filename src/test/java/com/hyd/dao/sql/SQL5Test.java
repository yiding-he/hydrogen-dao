package com.hyd.dao.sql;

import com.hyd.dao.database.commandbuilder.Command;
import org.junit.Test;

import java.util.Date;

public class SQL5Test {

    @Test
    public void test1() throws Exception {
        Command command = new SQL5() {{
            select(
                    "u.userid", "u.username"
            ).from(
                    "user u", "role r"
            ).matchAll(
                    where("u.last_login < ?", new Date()),
                    where("u.role_id > ?", 100),
                    where("u.role_id = r.id"),
                    matchAny(
                            where("u.username like ?", "John%"),
                            where("u.username like ?", "Mike%"),
                            where("u.username like ?", "Amy%")
                    )
            );
        }}.toCommand();
    }
}

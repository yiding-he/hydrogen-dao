package com.hyd.dao.database.commandbuilder;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class MappedCommandTest {

    @Test
    public void testToCommand() throws Exception {
        MappedCommand mappedCommand = new MappedCommand(
                "select * from USER where USERNAME=#username# and ROLE in (#role#)")
                .setParam("username", "admin")
                .setParam("role", new int[]{1, 2, 3, 4, 5, 6});

        Command command = mappedCommand.toCommand();
        System.out.println(command.getStatement());
        System.out.println(command.getParams());
    }

    @Test
    public void testToCommand2() throws Exception {
        MappedCommand cmd = new MappedCommand(
                "update USERS set ROLE=#role# where ID=#userid#")
                .setParam("role", "admin")
                .setParam("userid", 1);
    }
}
package com.hyd.daotests.benchmark;

import org.h2.tools.Server;

import java.sql.SQLException;

public class RunH2Server {

    public static void main(String[] args) throws SQLException {
        Server server = Server.createTcpServer(args);
        System.out.println("server port: " + server.getPort());
        server.start();
    }
}

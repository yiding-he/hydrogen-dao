package com.hyd.dao.demo;

import com.hyd.dao.DataSources;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class App {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private DataSources dataSources;

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}

package com.hyd.dao.demo;

import com.hyd.dao.DAO;
import com.hyd.dao.mate.util.ScriptExecutor;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class App {

    @Autowired
    private DAO dao;

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @PostConstruct
    public void init() {
        ScriptExecutor.execute("classpath:/init.sql", dao);
        log.info("Initialization completed.");

        dao.query("select * from book").forEach(row -> log.info(row.toString()));
    }
}

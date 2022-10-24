package com.hyd.dao.springboot.multidatasource;

import com.hyd.dao.DAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootApplication
public class MultiDataSourceTestApp {

    @Autowired
    private DAO dao1;

    @Autowired
    private DAO dao2;

    @Autowired
    private DAO dao3;

    public static void main(String[] args) {
        SpringApplication.run(MultiDataSourceTestApp.class, args);
    }

    @Bean
    public ApplicationRunner applicationRunner() {
        return args -> {
            assertFalse(dao1.query("select 1").isEmpty());
            assertFalse(dao2.query("select 1").isEmpty());
            assertFalse(dao3.query("select 1").isEmpty());
        };
    }

}

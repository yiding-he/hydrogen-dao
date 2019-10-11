package com.hyd.dao.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class SpringBootStarterApp {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootStarterApp.class, args);
    }
}

package com.hyd.dao.springboot;

import com.hyd.dao.DAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SpringBootTest {

    @Autowired
    private DAO dao;

    public static void main(String[] args) {
        SpringApplication.run(SpringBootTest.class, "--spring.profiles.active=h2");
    }

    @Bean
    public CommandLineRunner run() {
        return args -> {
            dao.query("show tables").forEach(System.out::println);
        };
    }
}

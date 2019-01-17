package com.hyd.dao.springboot;

import com.hyd.dao.DAO;
import com.hyd.dao.DataSources;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SpringBootTest {

    @Autowired
    private DAO dao;            // hydrogen-dao.data-sources.default

    @Autowired
    private DAO anotherDao;     // hydrogen-dao.data-sources.another

    public static void main(String[] args) {
        SpringApplication.run(SpringBootTest.class, "--spring.profiles.active=h2");
    }

    @Bean
    public DAO anotherDao(DataSources dataSources) {
        return dataSources.getDAO("another");
    }

    @Bean
    public CommandLineRunner run() {
        return args -> {
            System.out.println("dao tables:");
            dao.query("show tables").forEach(System.out::println);
            System.out.println("anotherDao tables:");
            anotherDao.query("show tables").forEach(System.out::println);
        };
    }
}

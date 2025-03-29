package com.hyd.daotests.springboot;

import com.hyd.dao.DAO;
import com.hyd.dao.spring.SpringAutoConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import javax.annotation.PostConstruct;

@SpringBootApplication
@Import(SpringAutoConfiguration.class)
public class SpringBootTestApp {

    @Autowired
    private DAO dao;

    public static void main(String[] args) {
        SpringApplication.run(SpringBootTestApp.class, args);
    }

    @PostConstruct
    public void test() {
        System.out.println("dao = " + dao);
    }
}

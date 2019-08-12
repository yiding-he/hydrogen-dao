package com.hyd.dao.springboot;

import com.hyd.dao.DAO;
import com.hyd.dao.DataSources;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SpringBootTest {

    @Autowired
    private DAO dao;

    public static void main(String[] args) {
        SpringApplication.run(SpringBootTest.class, "--debug=true --spring.profiles.active=h2");
    }

    @Bean
    public DAO anotherDao(DataSources dataSources) {
        return dataSources.getDAO("another");
    }

    @Bean
    public CommandLineRunner run() {
        return args -> {
            // dao.execute("create table user(id int primary key)");
            System.out.println("dao tables:");
            dao.query("show tables").forEach(System.out::println);
        };
    }

    @Bean
    @ConfigurationProperties("spring.datasource.ds1")
    public DataSource ds1(DataSources dataSources) {
        DataSource dataSource = DataSourceBuilder.create().build();
        dataSources.setDataSource("ds1", dataSource);
        return dataSource;
    }

    @Autowired
    private DataSources dataSources;

    public void showTables() {
        DAO ds1 = this.dataSources.getDAO("ds1");
        ds1.query("show tables").forEach(System.out::println);
    }
}

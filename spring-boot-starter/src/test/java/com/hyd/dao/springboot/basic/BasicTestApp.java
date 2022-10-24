package com.hyd.dao.springboot.basic;

import com.hyd.dao.DAO;
import com.hyd.dao.repository.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Date;
import java.util.List;

@SpringBootApplication
public class BasicTestApp {

    public static class User {

        private String id;

        private Date createTime;

        public User() {
        }

        public User(String id, Date createTime) {
            this.id = id;
            this.createTime = createTime;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Date getCreateTime() {
            return createTime;
        }

        public void setCreateTime(Date createTime) {
            this.createTime = createTime;
        }

        @Override
        public String toString() {
            return "User{" +
                "id='" + id + '\'' +
                ", createTime=" + createTime +
                '}';
        }
    }

    @Autowired
    private DAO dao;

    public static void main(String[] args) {
        SpringApplication.run(BasicTestApp.class, args);
    }

    @Bean
    public ApplicationRunner applicationRunner() {
        return args -> {
            dao.execute("drop table if exists basic_test_user");
            dao.execute("create table basic_test_user(id varchar(40) primary key, create_time datetime(6))");

            var repository = new Repository<>(User.class, dao, "basic_test_user");
            repository.insertBatch(List.of(
                new User("user1", new Date()),
                new User("user2", new Date()),
                new User("user3", new Date())
            ));
            System.out.println(repository.queryById("user1"));
        };
    }
}

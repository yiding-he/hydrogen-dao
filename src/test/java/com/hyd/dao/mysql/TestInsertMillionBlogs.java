package com.hyd.dao.mysql;

import com.hyd.dao.DAO;
import com.hyd.dao.src.models.Blog;

import java.util.ArrayList;
import java.util.List;

public class TestInsertMillionBlogs {

    // see resources/junit-rule-scripts/tables.sql to create blog table
    public static void main(String[] args) {

        DAO dao = TestBase.initDao();
        dao.execute("truncate table blog");
        long start = System.currentTimeMillis();

        for (int i = 0; i < 1000; i++) {
            List<Blog> blogs = new ArrayList<>();
            for (int j = 0; j < 1000; j++) {
                int id = i * 1000 + j;
                blogs.add(new Blog(id, "blog" + id, "content of blog " + id, false));
            }
            dao.insert(blogs, "blog");

            if (i % 10 == 0) {
                System.out.println((i * 1000) + " blogs inserted.");
            }
        }

        System.out.println("All blogs inserted in " + (System.currentTimeMillis() - start) + "ms");
    }

}

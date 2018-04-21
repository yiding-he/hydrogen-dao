package com.hyd.dao.repositories;

import com.hyd.dao.DAO;
import com.hyd.dao.SQL;
import com.hyd.dao.models.Blog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public class BlogRepository {

    @Autowired
    private DAO dao;

    public void setDao(DAO dao) {
        this.dao = dao;
    }

    public Blog queryById(Integer Id) {
        return dao.queryFirst(Blog.class,
                SQL.Select("*")
                        .From("BLOG")
                        .Where("ID = ?", Id)
        );
    }

    public List<Blog> queryAll() {
        return dao.query(Blog.class,
                SQL.Select("*")
                        .From("BLOG")
        );
    }

    public List<Blog> queryByLastUpdate(Date minLastUpdate, Date maxLastUpdate) {
        return dao.query(Blog.class,
                SQL.Select("*")
                        .From("BLOG")
                        .Where("LAST_UPDATE >= ?", minLastUpdate)
                        .And("LAST_UPDATE <= ?", maxLastUpdate)
        );
    }

    public void insert(Blog blog) {
        dao.insert(blog, "BLOG");
    }
}
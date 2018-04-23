package com.hyd.dao.src.repositories;

import com.hyd.dao.DAO;
import com.hyd.dao.SQL;
import com.hyd.dao.src.models.Blog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
public class BlogRepository {

    @Autowired
    private DAO dao;

    public void setDao(DAO dao) {
        this.dao = dao;
    }

    public Blog queryById(Integer id) {
        return dao.queryFirst(Blog.class, 
            SQL.Select("*")
            .From("BLOG")
            .Where("ID = ?", id)
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

    public void insert(Map<String, Object> map) {
        dao.insert(map, "BLOG");
    }

}
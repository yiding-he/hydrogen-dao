package com.hyd.dao.src.repositories;

import com.hyd.dao.DAO;
import com.hyd.dao.SQL;
import com.hyd.dao.src.models.Blog;
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

    public Blog queryById(Long id) {
        return dao.queryFirst(Blog.class, 
            SQL.Select("*")
            .From("blog")
            .Where("id = ?", id)
        );
    }

    public List<Blog> queryByCreateTime(Date minCreateTime, Date maxCreateTime) {
        return dao.query(Blog.class, 
            SQL.Select("*")
            .From("blog")
            .Where("create_time >= ?", minCreateTime)
            .And("create_time <= ?", maxCreateTime)
        );
    }

}
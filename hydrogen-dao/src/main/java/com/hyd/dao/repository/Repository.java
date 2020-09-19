package com.hyd.dao.repository;

import com.hyd.dao.DAO;

public class Repository<T> {

    private DAO dao;

    private String tableName;

    private Class<T> type;

    public Repository(Class<T> type, DAO dao, String tableName) {
        this.type = type;
        this.dao = dao;
        this.tableName = tableName;
    }

    public T findById(Object singlePrimaryKey) {
        return dao.find(type, tableName, singlePrimaryKey);
    }
}

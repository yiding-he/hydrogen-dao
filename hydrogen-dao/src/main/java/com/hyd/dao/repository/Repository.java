package com.hyd.dao.repository;

import com.hyd.dao.DAO;
import com.hyd.dao.command.Command;
import com.hyd.dao.command.builder.QueryCommandBuilder;
import com.hyd.dao.mate.util.ConnectionContext;
import com.hyd.dao.transaction.TransactionManager;

public class Repository<T> {

    private final DAO dao;

    private final String tableName;

    private final Class<T> type;

    public Repository(Class<T> type, DAO dao, String tableName) {
        this.type = type;
        this.dao = dao;
        this.tableName = tableName;
    }

    public T findById(Object singlePrimaryKey) {
        ConnectionContext context = TransactionManager.getConnectionContext(dao);
        QueryCommandBuilder builder = new QueryCommandBuilder(context);
        Command command = builder.buildByKey(tableName, singlePrimaryKey);
        return dao.queryFirst(type, command);
    }
}

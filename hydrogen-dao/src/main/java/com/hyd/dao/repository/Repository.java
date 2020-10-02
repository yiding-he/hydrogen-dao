package com.hyd.dao.repository;

import com.hyd.dao.DAO;
import com.hyd.dao.command.BatchCommand;
import com.hyd.dao.command.Command;
import com.hyd.dao.command.builder.DeleteCommandBuilder;
import com.hyd.dao.command.builder.InsertCommandBuilder;
import com.hyd.dao.command.builder.QueryCommandBuilder;
import com.hyd.dao.mate.util.ConnectionContext;
import com.hyd.dao.transaction.TransactionManager;

import java.util.List;

public class Repository<T> {

    private final DAO dao;

    private final String tableName;

    private final Class<T> type;

    public Repository(Class<T> type, DAO dao, String tableName) {
        this.type = type;
        this.dao = dao;
        this.tableName = tableName;
    }

    private ConnectionContext getContext() {
        return TransactionManager.getConnectionContext(this.dao);
    }

    public T queryById(Object singlePrimaryKey) {
        QueryCommandBuilder builder = new QueryCommandBuilder(getContext());
        Command command = builder.buildByKey(tableName, singlePrimaryKey);
        return dao.queryFirst(type, command);
    }

    public List<T> queryByInstance(T t) {
        QueryCommandBuilder builder = new QueryCommandBuilder(getContext());
        Command command = builder.build(tableName, t);
        return dao.query(type, command);
    }

    public int deleteById(Object singlePrimaryKey) {
        DeleteCommandBuilder builder = new DeleteCommandBuilder(getContext());
        Command command = builder.buildByKey(tableName, singlePrimaryKey);
        return dao.execute(command);
    }

    public int deleteByInstance(T t) {
        if (t == null) {
            return 0;
        }

        DeleteCommandBuilder builder = new DeleteCommandBuilder(getContext());
        Command command = builder.build(tableName, t);
        return dao.execute(command);
    }

    public int insertInstance(T t) {
        InsertCommandBuilder builder = new InsertCommandBuilder(getContext());
        Command command = builder.build(tableName, t);
        return dao.execute(command);
    }

    public int insertBatch(List<T> list) {
        InsertCommandBuilder builder = new InsertCommandBuilder(getContext());
        BatchCommand command = builder.buildBatch(tableName, list);
        return dao.execute(command);
    }
}

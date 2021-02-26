package com.hyd.dao.repository;

import com.hyd.dao.DAO;
import com.hyd.dao.command.BatchCommand;
import com.hyd.dao.command.Command;
import com.hyd.dao.command.builder.DeleteBuilder;
import com.hyd.dao.command.builder.InsertBuilder;
import com.hyd.dao.command.builder.QueryBuilder;
import com.hyd.dao.command.builder.UpdateBuilder;
import com.hyd.dao.database.ConnectionContext;
import com.hyd.dao.transaction.TransactionManager;

import java.util.List;
import java.util.function.Function;

public class Repository<T> {

    private final DAO dao;

    private final String tableName;

    private final Class<T> type;

    public Repository(Class<T> type, DAO dao, String tableName) {
        this.type = type;
        this.dao = dao;
        this.tableName = tableName;
    }

    private <E> E withConnectionContext(Function<ConnectionContext, E> f) {
        ConnectionContext context = TransactionManager.getConnectionContext(this.dao);
        try {
            return f.apply(context);
        } finally {
            context.closeIfAutoCommit();
        }
    }

    public T queryById(Object singlePrimaryKey) {
        Command command = withConnectionContext(
            context -> new QueryBuilder(context).buildByKey(tableName, singlePrimaryKey)
        );
        return dao.queryFirst(type, command);
    }

    public List<T> queryByInstance(T t) {
        Command command = withConnectionContext(
            context -> new QueryBuilder(context).build(tableName, t)
        );
        return dao.query(type, command);
    }

    public int deleteById(Object singlePrimaryKey) {
        Command command = withConnectionContext(
            context -> new DeleteBuilder(context).buildByKey(tableName, singlePrimaryKey)
        );
        return dao.execute(command);
    }

    public int deleteByInstance(T t) {
        if (t == null) {
            return 0;
        }

        Command command = withConnectionContext(
            context -> new DeleteBuilder(context).build(tableName, t)
        );
        return dao.execute(command);
    }

    public int insertInstance(T t) {
        Command command = withConnectionContext(
            context -> new InsertBuilder(context).build(tableName, t)
        );
        return dao.execute(command);
    }

    public int insertBatch(List<T> list) {
        BatchCommand command = withConnectionContext(
            context -> new InsertBuilder(context).buildBatch(tableName, list)
        );
        return dao.execute(command);
    }

    public int updateById(T t) {
        Command command = withConnectionContext(
            context -> new UpdateBuilder(context).buildByKey(tableName, t)
        );
        return dao.execute(command);
    }
}

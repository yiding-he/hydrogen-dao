package com.hyd.dao.repository;

import com.hyd.dao.*;
import com.hyd.dao.command.builder.DeleteBuilder;
import com.hyd.dao.command.builder.InsertBuilder;
import com.hyd.dao.command.builder.QueryBuilder;
import com.hyd.dao.command.builder.UpdateBuilder;
import com.hyd.dao.database.ConnectionContext;
import com.hyd.dao.transaction.TransactionManager;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 针对单张表提供封装的增删改查方法
 *
 * @param <T> 表对应的 entity 类型
 */
public class Repository<T> {

    protected final DAO dao;

    protected final String tableName;

    protected final Class<T> type;

    /**
     * 构造方法
     *
     * @param type      实体类型
     * @param dao       DAO 对象
     * @param tableName 表名，如果为空则尝试从实体类型的 @Table 注解中获取
     */
    public Repository(Class<T> type, DAO dao, String tableName) {
        this.type = type;
        this.dao = dao;
        this.tableName = parseTableName(type, tableName);
    }

    /**
     * 构造方法，默认使用实体类型的 @Table 注解获取表名
     *
     * @param type 实体类型
     * @param dao  DAO 对象
     */
    public Repository(Class<T> type, DAO dao) {
        this.type = type;
        this.dao = dao;
        this.tableName = parseTableName(type, null);
    }

    private String parseTableName(Class<T> type, String tableName) {
        if (tableName != null) {
            return tableName;
        } else if (type.isAnnotationPresent(Table.class)) {
            return type.getAnnotation(Table.class).name();
        }
        throw new DAOException("No table name specified");
    }

    private <E> E withConnectionContext(Function<ConnectionContext, E> f) {
        var context = TransactionManager.getConnectionContext(this.dao);
        try {
            return f.apply(context);
        } finally {
            context.closeIfAutoCommit();
        }
    }

    /**
     * 根据主键查询单条记录
     *
     * @param singlePrimaryKey 主键值
     *
     * @return 查询结果
     */
    public T queryById(Object singlePrimaryKey) {
        var command = withConnectionContext(
            context -> new QueryBuilder(context).buildByKey(tableName, singlePrimaryKey)
        );
        return dao.queryFirst(type, command);
    }

    /**
     * 根据实体类对象查询多条记录
     *
     * @param t 实体类对象
     *
     * @return 查询结果
     */
    public List<T> queryByInstance(T t) {
        var command = withConnectionContext(
            context -> new QueryBuilder(context).build(tableName, t)
        );
        return dao.query(type, command);
    }

    /**
     * 自定义条件查询
     *
     * @param incomplete 可用于补完查询条件的 Select 对象
     *
     * @return 查询结果
     */
    public List<T> query(Consumer<SQL.Select> incomplete) {
        var select = new SQL.Select("*").From(tableName);
        if (incomplete != null) {
            incomplete.accept(select);
        }
        return dao.query(type, select.toCommand());
    }

    /**
     * 自定义条件分页查询
     *
     * @param incomplete 可用于补完查询条件的 Select 对象
     * @param pageSize   每页记录数
     * @param pageIndex  页码，0 表示第一页
     *
     * @return 分页查询结果
     */
    public Page<T> queryPage(Consumer<SQL.Select> incomplete, int pageSize, int pageIndex) {
        var select = new SQL.Select("*").From(tableName);
        if (incomplete != null) {
            incomplete.accept(select);
        }
        return dao.queryPage(type, select.toCommand(), pageSize, pageIndex);
    }

    public int deleteById(Object singlePrimaryKey) {
        var command = withConnectionContext(
            context -> new DeleteBuilder(context).buildByKey(tableName, singlePrimaryKey)
        );
        return dao.execute(command);
    }

    public int deleteByInstance(T t) {
        if (t == null) {
            return 0;
        }

        var command = withConnectionContext(
            context -> new DeleteBuilder(context).build(tableName, t)
        );
        return dao.execute(command);
    }

    public int insertInstance(T t) {
        var command = withConnectionContext(
            context -> new InsertBuilder(context).build(tableName, t)
        );
        return dao.execute(command);
    }

    public int insertBatch(List<T> list) {
        var command = withConnectionContext(
            context -> new InsertBuilder(context).buildBatch(tableName, list)
        );
        return dao.execute(command);
    }

    public int updateById(T t) {
        var command = withConnectionContext(
            context -> new UpdateBuilder(context).buildByKey(tableName, t)
        );
        return dao.execute(command);
    }
}

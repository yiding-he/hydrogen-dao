package com.hyd.dao.database.executor;

import com.hyd.dao.DAOException;
import com.hyd.dao.Page;
import com.hyd.dao.Row;
import com.hyd.dao.command.BatchCommand;
import com.hyd.dao.command.Command;
import com.hyd.dao.command.IteratorBatchCommand;
import com.hyd.dao.database.ConnectionContext;
import com.hyd.dao.database.RowIterator;
import com.hyd.dao.database.dialects.Dialect;
import com.hyd.dao.database.type.NameConverter;
import com.hyd.dao.snapshot.ExecutorInfo;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.function.Consumer;

/**
 * 执行数据库操作的接口
 * <p>
 * Executor 持有 Connection 对象。如果没有在事务中，Executor 对象是一次性的，
 * 执行完第一个 SQL 命令后就会关闭连接（或返还给连接池），且不能再被使用；
 * 而如果在事务中，Executor 对象会被 TransactionManager 缓存起来，直到事务结束。
 *
 * @author <a href="mailto:yiding.he@gmail.com">yiding_he</a>
 */
@SuppressWarnings("rawtypes")
public abstract class Executor {

    protected ExecutorInfo info;            // 当前状态

    protected ConnectionContext context;    // 连接上下文

    protected ExecutionContext executionContext = new ExecutionContext(); // 本次执行上下文

    public Executor(ConnectionContext context) {
        this.info = new ExecutorInfo(context.getDataSourceName());
        this.context = context;
    }

    public ConnectionContext getContext() {
        return context;
    }

    protected Dialect getDialect() {
        return context.getDialect();
    }

    public void setAutoCommit(boolean autoCommit) {
        try {
            this.context.getConnection().setAutoCommit(autoCommit);
        } catch (SQLException e) {
            throw DAOException.wrap(e);
        }
    }

    /**
     * 判断连接是否已经关闭
     *
     * @return 如果链接已经关闭，则返回 true
     */
    public abstract boolean isClosed();

    /**
     * 执行 sql 语句
     *
     * @param sql    要执行的语句
     * @param params 参数
     *
     * @return 受影响的行数
     */
    public abstract int execute(String sql, List<Object> params);

    /**
     * 执行 SQL 命令
     *
     * @param command SQL 命令
     *
     * @return 受影响的行数
     */
    public int execute(Command command) {
        return execute(command.getStatement(), command.getParams());
    }

    /**
     * 执行批量 SQL 语句
     *
     * @param command 批量 SQL 语句
     *
     * @return 受影响的行数
     */
    public abstract int execute(BatchCommand command);

    /**
     * 流式执行批量 SQL 语句
     *
     * @param command 批量 SQL 语句
     *
     * @return 受影响的行数
     */
    public abstract int execute(IteratorBatchCommand command);

    /**
     * 查询分页
     *
     * @param clazz     包装类
     * @param sql       查询语句
     * @param params    参数
     * @param pageSize  分页大小
     * @param pageIndex 页号（从0开始）
     *
     * @return 查询的当前页
     */
    public abstract <T> Page<T> queryPage(Class<T> clazz, String sql, List<Object> params, int pageSize, int pageIndex);

    /**
     * @param clazz         包装类
     * @param sql           查询语句
     * @param params        参数
     * @param startPosition 获取查询结果的开始位置（包含）
     * @param endPosition   获取查询结果的结束位置（不包含）
     *
     * @return 查询结果。如果 startPosition &lt; 0 或 endPosition &lt; 0 则表示返回所有的查询结果
     */
    public abstract List query(Class clazz, String sql, List<Object> params, int startPosition, int endPosition);

    /**
     * 调用存储过程并返回结果
     *
     * @param name   存储过程名称
     * @param params 参数
     *
     * @return 调用结果
     */
    public abstract List call(String name, Object[] params);

    /**
     * 调用 Oracle 存储过程
     * 参见 {@link com.hyd.dao.DAO#callFunction}
     */
    public abstract List callFunction(String name, Object[] params);

    /**
     * 执行查询，返回迭代器
     *
     * @param sql          要执行的查询语句
     * @param params       查询参数
     * @param preProcessor 对 Row 对象的预处理
     *
     * @return 用于获得查询结果的迭代器
     */
    public abstract RowIterator queryIterator(String sql, List<Object> params, Consumer<Row> preProcessor);

    //////////////////////////////////////////////////////////////

    public ExecutorInfo getInfo() {
        return info;
    }

    protected NameConverter getNameConverter() {
        return this.context.getNameConverter();
    }

    protected Connection getConnection() {
        return this.context.getConnection();
    }

    /**
     * 关闭 executor 对象，如果当前不处于事务当中。
     */
    public void finish() {
        this.context.closeIfAutoCommit();
        if (info != null) {
            info.setClosed(true);
        }
    }

}

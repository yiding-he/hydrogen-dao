package com.hyd.dao.database.executor;

import com.hyd.dao.BatchCommand;
import com.hyd.dao.Page;
import com.hyd.dao.database.RowIterator;
import com.hyd.dao.database.TransactionManager;
import com.hyd.dao.snapshot.ExecutorInfo;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * 执行数据库操作的接口
 * 注意：凡是获取 Executor 对象的方法，必须在完成操作之后手动调用 finish 方法。
 *
 * @author <a href="mailto:yiding.he@gmail.com">yiding_he</a>
 */
public abstract class Executor {

    protected Connection connection;

    protected ExecutorInfo info;

    /**
     * 构造函数
     *
     * @param connection 数据库连接
     */
    public Executor(Connection connection) {
        this.connection = connection;
    }

    /**
     * 提交，关闭连接，释放资源
     */
    public abstract void close();

    /**
     * 判断连接是否已经关闭
     *
     * @return 如果链接已经关闭，则返回 true
     */
    public abstract boolean isClosed();

    /**
     * 回滚并关闭连接
     */
    public abstract void rollbackAndClose();

    /**
     * 执行 sql 语句
     *
     * @param sql    要执行的语句
     * @param params 参数
     *
     * @return 受影响的行数
     */
    public abstract int execute(String sql, List params);

    /**
     * 执行批量 SQL 语句
     *
     * @param command 批量 SQL 语句
     *
     * @return 受影响的行数
     */
    public abstract int execute(BatchCommand command);

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
    public abstract <T> Page<T> queryPage(Class<T> clazz, String sql, List params, int pageSize, int pageIndex);

    /**
     * @param clazz         包装类
     * @param sql           查询语句
     * @param params        参数
     * @param startPosition 获取查询结果的开始位置（包含）
     * @param endPosition   获取查询结果的结束位置（不包含）
     *
     * @return 查询结果。如果 startPosition < 0 或 endPosition < 0 则表示返回所有的查询结果
     */
    public abstract List query(Class clazz, String sql, List params, int startPosition, int endPosition);

    /**
     * 根据主键和表名查询指定的记录
     *
     * @param wrapperClass 包装类
     * @param key          主键
     * @param tableName    表名
     *
     * @return 记录。如果查询不到则返回 null
     */
    public abstract <T> T find(Class<T> wrapperClass, Object key, String tableName);

    /**
     * 插入记录
     *
     * @param object    要插入的记录
     * @param tableName 表名
     */
    public abstract void insert(Object object, String tableName);

    /**
     * 将一个 Map 对象插入数据库
     *
     * @param row       包含字段名和值的 Map 对象。
     * @param tableName 表名
     */
    public abstract void insertMap(Map row, String tableName);

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
     * 删除指定的数据库记录
     *
     * @param obj       用于指定记录的对象，只要主键有值即可。
     * @param tableName 表名
     *
     * @return 受影响的行数
     */
    public abstract int delete(Object obj, String tableName);

    /**
     * 执行查询，返回迭代器
     *
     * @param sql    要执行的查询语句
     * @param params 查询参数
     *
     * @return 用于获得查询结果的迭代器
     */
    public abstract RowIterator queryIterator(String sql, List params);

    public void setInfo(ExecutorInfo info) {
        this.info = info;
    }

    /**
     * 参见 {@link com.hyd.dao.DAO#callFunction}
     */
    public abstract List callFunction(String name, Object[] params);

    public void setTransactionIsolation(int level) throws SQLException {
        connection.setTransactionIsolation(level);
    }

    /**
     * 关闭 executor 对象，如果当前不处于事务当中。
     */
    public void finish() {
        if (!TransactionManager.isInTransaction()) {
            close();
        }
    }

    /**
     * 将一个 List 中的所有元素插入数据库
     *
     * @param list      List 对象
     * @param tableName 表名
     */
    public abstract void insertList(List list, String tableName);

    public abstract int deleteByKey(Object key, String tableName);

    public abstract boolean exists(Object obj, String tableName);

}

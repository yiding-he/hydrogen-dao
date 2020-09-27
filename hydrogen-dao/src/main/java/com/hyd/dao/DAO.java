package com.hyd.dao;

import com.hyd.dao.command.BatchCommand;
import com.hyd.dao.command.Command;
import com.hyd.dao.command.IteratorBatchCommand;
import com.hyd.dao.command.MappedCommand;
import com.hyd.dao.command.builder.DeleteCommandBuilder;
import com.hyd.dao.command.builder.InsertCommandBuilder;
import com.hyd.dao.database.ExecutorFactory;
import com.hyd.dao.database.RowIterator;
import com.hyd.dao.database.executor.Executor;
import com.hyd.dao.database.type.NameConverter;
import com.hyd.dao.log.Logger;
import com.hyd.dao.mate.util.BeanUtil;
import com.hyd.dao.mate.util.ConnectionContext;
import com.hyd.dao.mate.util.Str;
import com.hyd.dao.snapshot.Snapshot;
import com.hyd.dao.transaction.TransactionManager;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Main facade of hydrogen-dao. It is thread safe.
 */
@SuppressWarnings({"unused", "unchecked", "rawtypes", "RedundantSuppression", "UnusedReturnValue"})
public class DAO {

    public static final Date SYSDATE = new Date(0) {

        @Override
        public String toString() {
            return "SYSDATE";
        }
    };

    public static final int SYSDATE_TYPE = 33257679;

    private static final Logger LOG = Logger.getLogger(DAO.class);

    /////////////////////////////////////////////////////////

    /**
     * data source name
     */
    private final String dataSourceName;

    private final NameConverter nameConverter;

    /**
     * If it is out of current transaction
     */
    private boolean standAlone;

    public DAO(String dataSourceName) {
        this(dataSourceName, false, NameConverter.DEFAULT);
    }

    public DAO(String dataSourceName, NameConverter nameConverter) {
        this(dataSourceName, false, nameConverter);
    }

    public DAO(String dataSourceName, boolean standAlone) {
        this(dataSourceName, standAlone, NameConverter.DEFAULT);
    }

    /**
     * 构造方法
     *
     * @param dataSourceName 数据源名称，用于从 {@link DataSources} 获取数据库连接
     * @param standAlone     如果为 true，则表示避开当前数据库事务独立提交
     * @param nameConverter  名称转换规则
     */
    public DAO(String dataSourceName, boolean standAlone, NameConverter nameConverter) {
        this.dataSourceName = dataSourceName;
        this.standAlone = standAlone;
        this.nameConverter = nameConverter;
    }

    public NameConverter getNameConverter() {
        return nameConverter;
    }

    /////////////////////////// TRANSACTION //////////////////////////////

    /**
     * Runs a transaction.
     *
     * @param runnable Transaction procedure.
     */
    public static void runTransaction(Runnable runnable) throws TransactionException {
        runTransaction(TransactionManager.DEFAULT_ISOLATION_LEVEL, runnable);
    }

    /**
     * Runs a transaction with specified isolation level.
     *
     * @param isolation One of the following values, default is
     *                  {@link TransactionManager#DEFAULT_ISOLATION_LEVEL}：
     *                  <ul>
     *                  <li>{@link java.sql.Connection#TRANSACTION_NONE}</li>
     *                  <li>{@link java.sql.Connection#TRANSACTION_READ_COMMITTED}</li>
     *                  <li>{@link java.sql.Connection#TRANSACTION_READ_UNCOMMITTED}</li>
     *                  <li>{@link java.sql.Connection#TRANSACTION_REPEATABLE_READ}</li>
     *                  <li>{@link java.sql.Connection#TRANSACTION_SERIALIZABLE}</li>
     *                  </ul>
     * @param runnable  Transaction procedure.
     */
    public static void runTransaction(int isolation, Runnable runnable) {
        TransactionManager.start();
        TransactionManager.setTransactionIsolation(isolation);

        try {
            runnable.run();
            TransactionManager.commit();
        } catch (TransactionException e) {
            TransactionManager.rollback();
            throw e;
        } catch (Exception e) {
            TransactionManager.rollback();
            throw new TransactionException(e);
        }
    }

    /////////////////// QUERY //////////////////////

    /**
     * 将 sql id 替换为真实的 SQL 语句，以及去掉语句结尾的分号
     *
     * @param sql 可能以分号结尾的 sql 语句或 sql id
     *
     * @return 修复后的 sql 语句
     */
    private static String fixSql(String sql) {
        return Str.removeEnd(sql.trim(), ";");
    }

    /**
     * 获得一个连接池快照。不同的数据源使用不同的连接池。
     *
     * @param dsName 数据源名称
     *
     * @return 该数据源的连接池快照
     */
    public static Snapshot getSnapshot(String dsName) {
        return Snapshot.getInstance(dsName);
    }

    /**
     * 某些情况下用户需要的是包含 Map 对象的 List
     *
     * @param rowList 包含 Row 对象的 List
     *
     * @return 包含 Map 对象的 List
     */
    public static List<Map<String, Object>> toMapList(List<Row> rowList) {
        return new ArrayList<>(rowList);
    }

    /**
     * 判断本 DAO 对象是否是独立于当前事务之外
     *
     * @return 如果本 DAO 对象独立于事务之外，则返回 true
     */
    public boolean isStandAlone() {
        return this.standAlone;
    }

    void setStandAlone(boolean standAlone) {
        this.standAlone = standAlone;
    }

    /**
     * 获得 dao 实例所属的数据源名称
     *
     * @return dao 实例所属的数据源名称
     */
    public String getDataSourceName() {
        return dataSourceName;
    }

    private ConnectionContext getContext() {
        return TransactionManager.getConnectionContext(this);
    }

    ////////////////////////////////////////////////////////////////

    private void runWithExecutor(Consumer<Executor> consumer) {
        Executor executor = ExecutorFactory.getExecutor(this);
        try {
            consumer.accept(executor);
        } finally {
            executor.finish();
        }
    }

    private <T> T returnWithExecutor(Function<Executor, T> f) {
        Executor executor = ExecutorFactory.getExecutor(this);
        try {
            return f.apply(executor);
        } finally {
            executor.finish();
        }
    }

    ////////////////////////////////////////////////////////////////

    /**
     * 执行包装成 Command 对象的查询
     *
     * @param command 查询
     *
     * @return 查询结果
     */
    public List<Row> query(Command command) {
        return query(command.getStatement(), command.getParams());
    }

    /**
     * 执行包装成 Command 对象的查询，并将查询结果包装成指定的 Pojo 类对象
     *
     * @param clazz   要包装的类
     * @param command 查询
     *
     * @return 查询结果
     */
    public <T> List<T> query(Class<T> clazz, Command command) {
        return query(clazz, command.getStatement(), command.getParams());
    }

    public List<Row> query(MappedCommand mappedCommand) {
        return query(mappedCommand.toCommand());
    }

    public <T> List<T> query(Class<T> clazz, MappedCommand mappedCommand) {
        return query(clazz, mappedCommand.toCommand());
    }

    public List<Row> query(SQL.Generatable generatable) {
        return query(generatable.toCommand());
    }

    public <T> List<T> query(Class<T> clazz, SQL.Generatable generatable) {
        return query(clazz, generatable.toCommand());
    }

    /**
     * 执行带参数的查询
     *
     * @param sql    查询语句
     * @param params 参数
     *
     * @return 查询结果
     * @throws DAOException 如果发生数据库错误
     */
    public List<Row> query(String sql, Object... params) throws DAOException {
        return query(null, sql, params);
    }

    /**
     * 执行查询，并以对象的方式返回查询结果。
     *
     * @param clazz  查询结果封装类
     * @param sql    查询语句
     * @param params 参数
     *
     * @return 查询结果
     * @throws DAOException 如果发生数据库错误
     */
    public <T> List<T> query(Class<T> clazz, String sql, Object... params) throws DAOException {
        return queryRange(clazz, sql, -1, -1, params);
    }

    ////////////////////////////////////////////////////////////////

    public Row queryFirst(MappedCommand mappedCommand) {
        return queryFirst(mappedCommand.toCommand());
    }

    public <T> T queryFirst(Class<T> clazz, MappedCommand mappedCommand) {
        return queryFirst(clazz, mappedCommand.toCommand());
    }

    public Row queryFirst(Command command) {
        return queryFirst(command.getStatement(), command.getParams());
    }

    public <T> T queryFirst(Class<T> clazz, Command command) {
        return queryFirst(clazz, command.getStatement(), command.getParams());
    }

    /**
     * 返回第一个查询结果
     *
     * @param sql    查询语句
     * @param params 参数
     *
     * @return 查询结果
     * @throws DAOException 如果发生数据库错误
     */
    public Row queryFirst(String sql, Object... params) throws DAOException {
        List<Row> list = query(sql, params);
        return list == null || list.isEmpty() ? null : list.get(0);
    }

    /**
     * 返回第一个查询结果
     *
     * @param sql    查询语句
     * @param clazz  包装类
     * @param params 参数
     *
     * @return 查询结果
     * @throws DAOException 如果发生数据库错误
     */
    public <T> T queryFirst(Class<T> clazz, String sql, Object... params) throws DAOException {
        List<T> list = queryRange(clazz, sql, 0, 1, params);
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    public Row queryFirst(SQL.Generatable generatable) {
        Command command = generatable.toCommand();
        return queryFirst(command.getStatement(), command.getParams());
    }

    public <T> T queryFirst(Class<T> clazz, SQL.Generatable generatable) {
        Command command = generatable.toCommand();
        return queryFirst(clazz, command.getStatement(), command.getParams());
    }

    ////////////////////////////////////////////////////////////////

    public List<Row> queryRange(Command command, int startPosition, int endPosition) {
        return queryRange(command.getStatement(), startPosition, endPosition, command.getParams());
    }

    public <T> List<T> queryRange(Class<T> clazz, Command command, int startPosition, int endPosition) {
        return queryRange(clazz, command.getStatement(), startPosition, endPosition, command.getParams());
    }

    public List<Row> queryRange(SQL.Generatable generatable, int startPosition, int endPosition) {
        return queryRange(generatable.toCommand(), startPosition, endPosition);
    }

    public <T> List<T> queryRange(Class<T> clazz, SQL.Generatable generatable, int startPosition, int endPosition) {
        Command command = generatable.toCommand();
        return queryRange(clazz, command.getStatement(), startPosition, endPosition, command.getParams());
    }

    /**
     * 执行指定位置范围的带参数查询
     *
     * @param sql           查询语句
     * @param startPosition 获取查询结果的开始位置（包含）
     * @param endPosition   获取查询结果的结束位置（不包含）
     * @param params        参数
     *
     * @return 查询结果
     * @throws DAOException         如果发生数据库错误
     * @throws NullPointerException 如果 sql 为 null
     */
    public List<Row> queryRange(String sql, int startPosition, int endPosition, Object... params) throws DAOException {
        return queryRange(null, sql, startPosition, endPosition, params);
    }

    /**
     * 执行指定位置范围的带参数查询
     *
     * @param clazz         查询结果包装类
     * @param sql           查询语句
     * @param startPosition 获取查询结果的开始位置（包含）
     * @param endPosition   获取查询结果的结束位置（不包含）
     * @param params        参数。如果是一个 List，则自动转换为 Array。
     *
     * @return 查询结果。如果 startPosition &lt; 0 或 endPosition &lt; 0 则表示返回所有的查询结果
     * @throws DAOException 如果发生数据库错误
     */
    public <T> List<T> queryRange(
        Class<T> clazz, String sql, int startPosition, int endPosition, Object... params) throws DAOException {

        if (params.length == 1 && params[0] instanceof List) {
            List list = (List) params[0];
            return queryRange(clazz, sql, startPosition, endPosition, list.toArray(new Object[0]));
        }

        String fixedSql = fixSql(sql);

        return returnWithExecutor(executor -> executor.query(
            clazz, fixedSql, Arrays.asList(params), startPosition, endPosition)
        );
    }

    ////////////////////////////////////////////////////////////////

    public Page<Row> queryPage(SQL.Generatable generatable, int pageSize, int pageIndex) {
        Command command = generatable.toCommand();
        return queryPage(null, command.getStatement(), pageSize, pageIndex, command.getParams());
    }

    public <T> Page<T> queryPage(Class<T> clazz, SQL.Generatable generatable, int pageSize, int pageIndex) {
        Command command = generatable.toCommand();
        return queryPage(clazz, command.getStatement(), pageSize, pageIndex, command.getParams());
    }

    /**
     * 执行分页查询
     *
     * @param sql       查询命令
     * @param params    参数
     * @param pageSize  页大小
     * @param pageIndex 页号
     *
     * @return 查询结果
     * @throws DAOException 如果发生数据库错误
     */
    public Page<Row> queryPage(String sql, int pageSize, int pageIndex, Object... params) throws DAOException {
        return queryPage(null, sql, pageSize, pageIndex, params);
    }

    /**
     * 执行分页查询
     *
     * @param wrappingClass 查询结果包装类
     * @param sql           查询命令
     * @param params        参数
     * @param pageSize      页大小
     * @param pageIndex     页号
     *
     * @return 查询结果
     * @throws DAOException 如果发生数据库错误
     */
    public <T> Page<T> queryPage(
        Class<T> wrappingClass, String sql,
        int pageSize, int pageIndex, Object... params) throws DAOException {
        if (params.length == 1 && params[0] instanceof List) {
            List list = (List) params[0];
            return queryPage(wrappingClass, sql, pageSize, pageIndex, list.toArray(new Object[0]));
        }

        String fixedSql = fixSql(sql);

        return returnWithExecutor(executor -> executor.queryPage(
            wrappingClass, fixedSql, Arrays.asList(params), pageSize, pageIndex)
        );
    }

    ////////////////////////////////////////////////////////////////

    public RowIterator queryIterator(SQL.Generatable<SQL.Select> generatable) throws DAOException {
        return queryIterator(generatable.toCommand());
    }

    public RowIterator queryIterator(Command command) throws DAOException {
        return queryIterator(command.getStatement(), command.getParams());
    }

    public RowIterator queryIterator(SQL.Generatable<SQL.Select> generatable, Consumer<Row> preProcessor) throws DAOException {
        return queryIterator(generatable.toCommand(), preProcessor);
    }

    public RowIterator queryIterator(Command command, Consumer<Row> preProcessor) throws DAOException {
        return queryIterator(command.getStatement(), preProcessor, command.getParams());
    }

    public RowIterator queryIterator(String sql, Object... params) throws DAOException {
        return queryIterator(sql, null, params);
    }

    /**
     * 执行查询，返回迭代器
     *
     * <strong>注意：不关闭迭代器的话，可能造成数据库连接泄露！</strong>
     *
     * @param sql    要执行的查询语句
     * @param params 查询参数
     *
     * @return 用于获得查询结果的迭代器。如果查询语句为 null，则返回 null。
     * @throws IllegalArgumentException 如果 sql 为 null
     * @throws DAOException             如果查询失败
     */
    public RowIterator queryIterator(String sql, Consumer<Row> preProcessor, Object... params) throws DAOException {

        if (sql == null) {
            throw new IllegalArgumentException("SQL is null");
        }

        if (params.length == 1 && params[0] instanceof List) {
            List list = (List) params[0];
            return queryIterator(sql, preProcessor, list.toArray(new Object[0]));
        }

        String fixedSql = fixSql(sql);
        List<Object> paramList = Arrays.asList(params);

        return returnWithExecutor(executor ->
            executor.queryIterator(fixedSql, paramList, preProcessor));
    }

    /**
     * 执行 select count 语句，并直接返回结果内容
     *
     * @param command 查询命令
     *
     * @return 结果中的数字
     */
    public long count(Command command) {
        Row row = queryFirst(command);
        Iterator<Object> iterator = row.values().iterator();
        return ((BigDecimal) iterator.next()).longValue();
    }

    /**
     * 执行 select count 语句，并直接返回结果内容
     *
     * @param sql    SQL 语句
     * @param params 参数
     *
     * @return 结果中的数字
     */
    public long count(String sql, Object... params) {
        Row row = queryFirst(sql, params);
        Iterator<Object> iterator = row.values().iterator();
        return ((BigDecimal) iterator.next()).longValue();
    }

    /**
     * 执行 select count 语句，并直接返回结果内容
     *
     * @param generatable 语句
     *
     * @return 结果中的数字
     */
    public long count(SQL.Generatable generatable) {
        Row row = queryFirst(generatable);
        Iterator<Object> iterator = row.values().iterator();
        return ((BigDecimal) iterator.next()).longValue();
    }

    /////////////////// UPDATE //////////////////////

    /**
     * 删除指定的一条记录
     *
     * @param obj       用于指定记录的对象，只要主键有值即可。
     * @param tableName 表名
     *
     * @return 受影响的行数
     * @throws DAOException 如果执行数据库操作失败
     */
    public int delete(Object obj, String tableName) throws DAOException {
        return returnWithExecutor(executor -> {
            Command command = new DeleteCommandBuilder(getContext()).build(tableName, obj);
            return executor.execute(command);
        });
    }

    public int deleteByKey(Object key, String tableName) {
        return returnWithExecutor(executor -> {
            Command command = new DeleteCommandBuilder(getContext()).buildByKey(tableName, key);
            return executor.execute(command);
        });
    }

    /**
     * 执行 SQL 语句
     *
     * @param sql    要执行的语句
     * @param params 参数
     *
     * @return 受影响的行数
     * @throws DAOException 如果发生数据库错误
     */
    public int execute(String sql, Object... params) throws DAOException {
        if (sql == null) {
            return 0;
        }
        String fixedSql = fixSql(sql);

        List<Object> paramsList;
        if (params.length == 1 && params[0] instanceof List) {
            paramsList = (List<Object>) params[0];
        } else {
            paramsList = Arrays.asList(params);
        }

        Command command = new Command(fixedSql, paramsList);
        return returnWithExecutor(executor -> executor.execute(command));
    }

    /**
     * 执行批量语句
     *
     * @param command 批量语句
     *
     * @return 受影响的行数
     * @throws DAOException 如果发生数据库错误
     */
    public int execute(BatchCommand command) throws DAOException {
        return returnWithExecutor(executor -> executor.execute(command));
    }

    /**
     * 执行流式批处理命令
     *
     * @param command 流式批处理命令
     *
     * @return 受影响的行数
     * @throws DAOException 如果执行失败
     */
    public int execute(IteratorBatchCommand command) throws DAOException {
        return returnWithExecutor(executor -> executor.execute(command));
    }

    public int execute(SQL.Generatable generatable) {

        // 当 Generatable 发现无法生成可执行的 SQL 时，将返回 null
        Command command = generatable.toCommand();

        if (command != null) {
            return execute(command);
        } else {
            LOG.error("无法执行的语句：" + generatable.getClass());
            return 0;
        }
    }

    /**
     * 执行命令
     *
     * @param command 要执行的命令
     *
     * @return 受影响的行数
     * @throws DAOException 如果发生数据库错误
     */
    public int execute(Command command) throws DAOException {
        return execute(command.getStatement(), command.getParams());
    }

    /**
     * 插入一条记录
     *
     * @param object    封装记录的对象
     * @param tableName 表名
     *
     * @throws DAOException 如果发生数据库错误
     */
    public void insert(Object object, String tableName) throws DAOException {
        if (object instanceof List) {
            insert((List) object, tableName);
            return;
        } else if (object instanceof Map) {
            insert((Map) object, tableName);
            return;
        }

        runWithExecutor(executor -> {
            Command command = new InsertCommandBuilder(getContext()).build(tableName, object);
            executor.execute(command);
        });
    }

    /**
     * 插入一条记录
     *
     * @param row       包含字段和值的记录
     * @param tableName 表名
     *
     * @throws DAOException 如果发生数据库错误
     */
    private void insert(Map row, String tableName) throws DAOException {
        runWithExecutor(executor -> {
            Command command = new InsertCommandBuilder(getContext()).build(tableName, row);
            executor.execute(command);
        });
    }

    /**
     * 插入一条记录
     *
     * @param obj 封装记录的对象
     *
     * @throws DAOException 如果发生数据库错误
     */
    public void insert(Object obj) throws DAOException {
        insert(obj, BeanUtil.getTableName(obj.getClass()));
    }

    /**
     * 批量插入记录
     *
     * @param objects   封装记录的对象
     * @param tableName 表名
     *
     * @throws DAOException 如果发生数据库错误
     */
    public void insert(List objects, String tableName) throws DAOException {
        if (objects == null || objects.isEmpty()) {
            return;
        }

        runWithExecutor(executor -> {
            BatchCommand command = new InsertCommandBuilder(getContext()).buildBatch(tableName, objects);
            executor.execute(command);
        });
    }

    /**
     * 调用存储过程
     *
     * @param name   存储过程名称
     * @param params 存储过程参数值
     *
     * @return 调用结果
     */
    public List<Object> call(String name, Object... params) {
        if (params.length == 1 && params[0] instanceof List) {
            List list = (List) params[0];
            return call(name, list.toArray(new Object[0]));
        }

        return returnWithExecutor(executor -> executor.call(name, params));
    }

    /**
     * 调用函数 (Oracle only)
     *
     * @param name   function 名称
     * @param params 调用参数
     *
     * @return 调用结果。第一个元素是 function 的返回值，第二个元素是第一个 OUT 或 IN_OUT 类型的参数，以此类推。
     * @throws DAOException 如果调用失败
     */
    public List callFunction(String name, Object... params) throws DAOException {
        if (params.length == 1 && params[0] instanceof List) {
            List list = (List) params[0];
            callFunction(name, list.toArray(new Object[0]));
        }

        return returnWithExecutor(executor -> executor.callFunction(name, params));
    }
}

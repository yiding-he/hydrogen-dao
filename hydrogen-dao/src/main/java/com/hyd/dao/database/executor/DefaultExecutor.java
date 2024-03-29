package com.hyd.dao.database.executor;

import com.hyd.dao.DAOException;
import com.hyd.dao.Page;
import com.hyd.dao.Row;
import com.hyd.dao.command.BatchCommand;
import com.hyd.dao.command.IteratorBatchCommand;
import com.hyd.dao.database.ConnectionContext;
import com.hyd.dao.database.RowIterator;
import com.hyd.dao.database.dialects.Dialects;
import com.hyd.dao.database.function.FunctionHelper;
import com.hyd.dao.log.Logger;
import com.hyd.dao.mate.util.Arr;
import com.hyd.dao.mate.util.ResultSetUtil;
import com.hyd.dao.mate.util.Str;
import com.hyd.dao.mate.util.TypeUtil;
import com.hyd.dao.sp.SpParam;
import com.hyd.dao.sp.SpParamType;
import com.hyd.dao.sp.StorageProcedureHelper;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/**
 * Default implementation of Executor
 *
 * @author <a href="mailto:yiding.he@gmail.com">Yiding He</a>
 */
@SuppressWarnings("MagicConstant")
public class DefaultExecutor extends Executor {

    private static final Logger LOG = Logger.getLogger(DefaultExecutor.class.getName() + ".sql");

    private static final Logger BATCH_LOG = Logger.getLogger(DefaultExecutor.class.getName() + ".batch");

    private static final int TIMEOUT = Integer.parseInt(Str.defaultIfEmpty(System.getProperty("jdbc.timeout"), "-1"));

    private Statement st;

    private ResultSet rs;

    public DefaultExecutor(ConnectionContext context) {
        super(context);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Page queryPage(Class clazz, String sql, List params, int pageSize, int pageIndex) {

        // startPos 从 0 开始算起，包含
        var startPos = pageIndex * pageSize;

        // endPos 不包含
        var endPos = startPos + pageSize;

        String rangedSql = null;
        try {
            rangedSql = getRangedSql(sql, startPos, endPos);

            printCommand(rangedSql == null ? sql : rangedSql, params);
            executeQuery(rangedSql == null ? sql : rangedSql, params);

            // 如果生成了分页语句，则读取所有结果，否则读取部分结果。
            Page result;
            if (rangedSql != null) {
                result = ResultSetUtil.readPageResultSet(rs, clazz, getNameConverter(), -1, -1);
            } else {
                result = ResultSetUtil.readPageResultSet(rs, clazz, getNameConverter(), pageSize, pageIndex);
            }

            result.setTotal(queryCount(sql, params));
            result.setPageSize(pageSize);
            result.setPageIndex(pageIndex);

            LOG.debug(findCaller() + "|Query result：" + result.size() + "/" + result.getTotal() + " records.");
            return result;
        } catch (Exception e) {
            throw new DAOException("Query failed:" + e.getMessage(), e, rangedSql == null ? sql : rangedSql, params);
        } finally {
            closeButConnection();
        }
    }

    /**
     * 获得一个包装好的分页查询语句，针对不同数据库应有不同实现。
     *
     * @param sql      查询语句
     * @param startPos 开始位置（0 开始，包含）
     * @param endPos   结束位置（不包含）
     *
     * @return 包装好的分页查询语句。对于未知类型的数据库，返回 null。
     */
    private String getRangedSql(String sql, int startPos, int endPos) {
        return getDialect().wrapRangeQuery(sql, startPos, endPos);
    }

    /**
     * 获得 sql 语句所查询出来的总记录数。本方法将 sql 中第一个 select 和第一个 from 之间的内容替换为 count(*)，然后获取查询结果。
     *
     * @param sql    要查询的语句
     * @param params 查询参数
     *
     * @return 查询结果的总数量
     */
    @SuppressWarnings("unchecked")
    private int queryCount(String sql, List params) {
        try {
            var countSql = getCountSql(sql);
            List<Row> list = query(null, countSql, params, -1, -1);
            if (!list.isEmpty()) {
                var map = list.get(0);
                return map.getInteger("cnt", 0);
            } else {
                return 0;
            }
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    private String getCountSql(String sql) throws SQLException {
        return getDialect().wrapCountQuery(sql);
    }

    @Override
    public RowIterator queryIterator(String sql, List<Object> params, Consumer<Row> preProcessor) {
        printCommand(sql, params);
        try {
            executionContext.setExecuteMode(ExecuteMode.Streaming);
            executeQuery(sql, params);
        } catch (SQLException e) {
            throw new DAOException("Query failed:", e, sql, params);
        }
        var rowIterator = new RowIterator(rs, preProcessor);
        rowIterator.setNameConverter(getNameConverter());
        return rowIterator;
    }

    /**
     * 执行查询
     *
     * @param sql           查询语句
     * @param clazz         封装类
     * @param params        参数
     * @param startPosition 开始位置（0 开始，包含）
     * @param endPosition   结束位置（不包含）
     *
     * @return 查询结果
     */
    @Override
    public List query(Class clazz, String sql, List<Object> params, int startPosition, int endPosition) {

        String rangedSql = null;
        try {
            rangedSql = endPosition <= 0 ?
                    null : getRangedSql(sql, startPosition, endPosition);

            printCommand(rangedSql == null ? sql : rangedSql, params);
            executeQuery(rangedSql == null ? sql : rangedSql, params);

            List<Object> result;
            if (rangedSql != null) {
                result = ResultSetUtil.readResultSet(rs, clazz, getNameConverter(), -1, -1);
            } else {
                result = ResultSetUtil.readResultSet(rs, clazz, getNameConverter(), startPosition, endPosition);
            }

            LOG.debug(findCaller() + "|Query result: " + result.size() + " records.");
            return result;
        } catch (Exception e) {
            throw new DAOException("Query failed:", e, rangedSql == null ? sql : rangedSql, params);
        } finally {
            closeButConnection();
        }
    }

    // 执行语句并将结果赋值给 this.rs
    private void executeQuery(String sql, List<Object> params) throws SQLException {

        // PreparedStatement 可以不用就不用，以免占用过多 Oracle 的指针。
        if (params == null || params.isEmpty()) {
            st = createNormalStatement();
            fixStatement(st);
            if (TIMEOUT != -1) {
                st.setQueryTimeout(TIMEOUT);
            }
            rs = st.executeQuery(sql);
        } else {
            var ps = createPreparedStatement(sql);
            fixStatement(ps);
            st = ps;
            insertParams(params);
            if (TIMEOUT != -1) {
                ps.setQueryTimeout(TIMEOUT);
            }
            rs = ps.executeQuery();
        }
    }

    private void fixStatement(Statement st) throws SQLException {
        Dialects.getDialect(context.getConnection()).setupStatement(st, executionContext.getExecuteMode());
    }

    ////////////////////////////////////////////////////////////////

    @Override
    public int execute(BatchCommand command) {

        if (command == BatchCommand.EMPTY) {
            return 0;
        }

        printBatchCommand(command);
        try {
            // 执行语句
            var params = command.getParams();
            var ps = createPreparedStatement(command.getCommand());
            st = ps;

            for (var param : params) {
                insertParams(param);
                ps.addBatch();
            }

            var counter = 0;
            var counts = ps.executeBatch();
            for (var count : counts) {
                if (count == Statement.SUCCESS_NO_INFO) {
                    counter++;
                } else {
                    counter += count;
                }
            }

            return counter;
        } catch (SQLException e) {
            throw new DAOException("Insert Failed: " + e.getMessage(),
                    e, command.getCommand(), command.getParams());
        } finally {
            closeButConnection();
        }
    }

    @Override
    public int execute(IteratorBatchCommand command) {

        var batchSize = command.getBatchSize();
        var counter = 0;
        if (batchSize < 1) {
            throw new IllegalStateException("Batch command size must > 0");
        }

        var params = command.getParams();
        List<List<Object>> buffer = new ArrayList<>(batchSize);

        while (params.hasNext()) {
            var next = params.next();

            buffer.add(next);
            if (buffer.size() >= batchSize) {
                counter += execute(new BatchCommand(command.getCommand(), buffer));
                buffer = new ArrayList<>(batchSize);
            }
        }

        // flush final data
        if (!buffer.isEmpty()) {
            counter += execute(new BatchCommand(command.getCommand(), buffer));
        }

        return counter;
    }

    @Override
    public int execute(String sql, List<Object> params) throws DAOException {
        printCommand(sql, params);
        try {
            // 执行语句
            if (params == null || params.isEmpty()) {
                st = createNormalStatement();
                if (TIMEOUT != -1) {
                    st.setQueryTimeout(TIMEOUT);
                }
                st.executeUpdate(sql);
            } else {
                var ps = createPreparedStatement(sql);
                st = ps;
                insertParams(params);
                if (TIMEOUT != -1) {
                    ps.setQueryTimeout(TIMEOUT);
                }
                ps.executeUpdate();
            }

            return st.getUpdateCount();
        } catch (SQLException e) {
            throw new DAOException("Execution failed: " + e.getMessage(), e, sql, params);
        } finally {
            closeButConnection();
        }
    }

    /**
     * 填入参数值
     *
     * @param params     参数值。元素的个数要和语句中的 ? 数量一致。
     * @throws SQLException 如果插入参数失败
     */
    private void insertParams(List<Object> params) throws SQLException {
        var ps = (PreparedStatement) st;
        for (var i = 0; i < params.size(); i++) {
            var value = params.get(i);

            if (value != null) {
                value = TypeUtil.convertParamValue(value);
                ps.setObject(i + 1, value);
            } else {
                ps.setNull(i + 1, Types.NULL);
            }
        }
    }

    ////////////////////////////////////////////////////////////////

    @Override
    public List call(String name, Object[] params) {
        try {
            var spParams = StorageProcedureHelper.createSpParams(name, params, getConnection());
            LOG.debug(findCaller() + "(procedure)" + name + Arrays.asList(spParams));
            var cs = StorageProcedureHelper.createCallableStatement(name, spParams, getConnection());
            if (TIMEOUT != -1) {
                cs.setQueryTimeout(TIMEOUT);
            }
            cs.executeQuery();
            return readResult(spParams, cs);
        } catch (SQLException e) {
            throw new DAOException("Procedure failed: " + e.getMessage(), e, name, Arrays.asList(params));
        }
    }

    @Override
    public List callFunction(String name, Object[] params) {

        try {
            LOG.debug(findCaller() + "(function)" + name + Arrays.asList(params));
            var spParams = FunctionHelper.createFunctionParams(name, params, getConnection());
            var resultType = spParams[0].getSqlType();

            // 去掉第一个
            spParams = Arr.subarray(spParams, 1, spParams.length);

            var cs = FunctionHelper.createCallableStatement(name, resultType, spParams, getConnection());
            if (TIMEOUT != -1) {
                cs.setQueryTimeout(TIMEOUT);
            }
            cs.executeQuery();
            return readResult(createOutputParams(resultType, spParams), cs);
        } catch (DAOException e) {
            throw e;
        } catch (Exception e) {
            throw new DAOException("Function failed: " + e.getMessage(), e, name, Arrays.asList(params));
        }
    }

    /**
     * 因为 function 有返回值，所以需要在调用参数前面加上一个输出参数。
     *
     * @param resultType 返回值类型
     * @param params     调用参数
     *
     * @return 用于接受返回值的参数组
     */
    private SpParam[] createOutputParams(int resultType, SpParam[] params) {
        var outParams = new SpParam[params.length + 1];
        System.arraycopy(params, 0, outParams, 1, params.length);
        outParams[0] = new SpParam(SpParamType.OUT, resultType, null);
        return outParams;
    }

    /**
     * 读取存储过程调用结果
     *
     * @param params 调用参数
     * @param cs     执行过的 CallableStatement
     *
     * @return 调用结果（数字值将被统一转为 BigDecimal）
     */
    private List readResult(SpParam[] params, CallableStatement cs) {
        List<Object> result = new ArrayList<>();
        try {
            for (var i = 0; i < params.length; i++) {
                var param = params[i];
                if (param.getType() == SpParamType.OUT || param.getType() == SpParamType.IN_OUT) {
                    var value = cs.getObject(i + 1);
                    var parsedValue = getDialect().parseCallableStatementResult(param.getSqlType(), value);
                    result.add(parsedValue);
                }
            }
        } catch (DAOException e) {
            throw e;
        } catch (Exception e) {
            throw new DAOException("Reading result failed: " + e.getMessage(), e);
        }
        return result;
    }

    /**
     * 创建一个 Statement 对象
     *
     * @return Statement 对象
     *
     * @throws SQLException 如果创建失败
     */
    private Statement createNormalStatement() throws SQLException {
        return getConnection().createStatement(getResultSetType(), ResultSet.CONCUR_READ_ONLY);
    }

    private PreparedStatement createPreparedStatement(String sql) throws SQLException {
        return getConnection().prepareStatement(sql, getResultSetType(), ResultSet.CONCUR_READ_ONLY);
    }

    private int getResultSetType() {
        // 创建 ResultSet 时类型参数值的选择:
        // MySQL/Oracle 等多数数据库可以指定为 TYPE_FORWARD_ONLY，且在查询时可以调用 ResultSet.absolute() 方法向前定位；
        // http://download.oracle.com/docs/cd/B10500_01/java.920/a96654/resltset.htm#1023726
        // 但是 MS SQLServer 就必须是 TYPE_SCROLL_SENSITIVE，否则调用 ResultSet.absolute() 就会报错。
        // 其他类型数据库兼容性尚未确定
        return getDialect().resultSetTypeForReading();
    }

    ////////////////////////////////////////////////////////////////

    private void printCommand(String sql, List params) {
        info.setLastCommand(sql);
        info.setLastExecuteTime(new java.util.Date());

        LOG.debug(findCaller() + "(" + context.getDataSourceName() + "): " +
                sql.replaceAll("\n", " ") + " " + (params == null ? "" : params.toString()));
    }

    private String findCaller() {

        var traceElements = Thread.currentThread().getStackTrace();
        var daoStarted = false;

        for (var traceElement : traceElements) {
            var className = traceElement.getClassName();
            var lineNumber = traceElement.getLineNumber();

            if (!daoStarted) {
                if (className.startsWith("com.hyd.dao.")) {
                    daoStarted = true;
                }
            } else {
                if (!className.startsWith("com.hyd.dao.")) {
                    return className + ":" + lineNumber;
                }
            }
        }
        return "";
    }

    private void printBatchCommand(BatchCommand command) {
        var sql = Str.n(command.getCommand());
        info.setLastCommand(sql);
        info.setLastExecuteTime(new java.util.Date());

        List<List<Object>> params = command.getParams() == null ? new ArrayList<>() : command.getParams();

        if (BATCH_LOG.isEnabled(Logger.Level.Debug)) {
            BATCH_LOG.debug("Batch(" + context.getDataSourceName() + "):" + sql.replaceAll("\n", " ") + "; parameters:");
            for (var param : params) {
                BATCH_LOG.debug(param.toString());
            }

        } else {
            if (LOG.isEnabled(Logger.Level.Debug)) {
                LOG.debug("Execute batch:" + sql.replaceAll("\n", " ") + "[" + params.size() + " groups]");
            }
        }
    }

    /**
     * Peace.
     */
    private void closeButConnection() {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                LOG.error("", e);
            }
        }

        if (st != null) {
            try {
                st.close();
            } catch (SQLException e) {
                LOG.error("", e);
            }
        }
    }

    private void closeConnection() {
        try {
            var connection = getConnection();
            if (!connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            LOG.error("", e);
        }
    }

    @Override
    public boolean isClosed() {
        try {
            var connection = getConnection();
            return connection == null || connection.isClosed();
        } catch (SQLException e) {
            LOG.error("Error checking connection: " + e.getMessage(), e);
            return true;
        }
    }

}

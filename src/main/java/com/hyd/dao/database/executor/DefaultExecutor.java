package com.hyd.dao.database.executor;

import com.hyd.dao.BatchCommand;
import com.hyd.dao.DAOException;
import com.hyd.dao.Page;
import com.hyd.dao.Row;
import com.hyd.dao.database.RowIterator;
import com.hyd.dao.database.commandbuilder.Command;
import com.hyd.dao.database.commandbuilder.DeleteCommandBuilder;
import com.hyd.dao.database.commandbuilder.InsertCommandBuilder;
import com.hyd.dao.database.commandbuilder.QueryCommandBuilder;
import com.hyd.dao.database.connection.ConnectionUtil;
import com.hyd.dao.database.function.FunctionHelper;
import com.hyd.dao.log.Logger;
import com.hyd.dao.sp.SpParam;
import com.hyd.dao.sp.SpParamType;
import com.hyd.dao.sp.StorageProsedureHelper;
import com.hyd.dao.util.ResultSetUtil;
import com.hyd.dao.util.StringUtil;
import com.hyd.dao.util.TypeUtil;
import oracle.jdbc.OracleTypes;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Default implementation of Executor
 *
 * @author <a href="mailto:yiding.he@gmail.com">Yiding He</a>
 */
public class DefaultExecutor extends Executor {

    static final Logger LOG = Logger.getLogger(DefaultExecutor.class);

    static final Logger BATCH_LOG = Logger.getLogger(DefaultExecutor.class.getName() + ".batch");

    static final int TIMEOUT = Integer.parseInt(StringUtils.defaultIfEmpty(System.getProperty("jdbc.timeout"), "-1"));

    private Statement st;

    private ResultSet rs;

    /**
     * 构造函数
     *
     * @param connection 数据库连接
     */
    public DefaultExecutor(Connection connection) {
        super(connection);
    }

    @SuppressWarnings("unchecked")
    public Page queryPage(Class clazz, String sql, List params, int pageSize, int pageIndex) {

        // startPos 从 0 开始算起，包含
        int startPos = pageIndex * pageSize;

        // endPos 不包含
        int endPos = startPos + pageSize;

        String rangedSql = getRangedSql(sql, startPos, endPos);

        if (rangedSql != null) {
            printCommand(rangedSql, params);
        } else {
            printCommand(sql, params);
        }

        try {

            executeQuery(rangedSql == null ? sql : rangedSql, params);

            // 如果生成了分页语句，则读取所有结果，否则读取部分结果。
            Page result;
            if (rangedSql != null) {
                result = ResultSetUtil.readPageResultSet(rs, clazz, -1, -1);
            } else {
                result = ResultSetUtil.readPageResultSet(rs, clazz, pageSize, pageIndex);
            }

            result.setTotal(queryCount(sql, params));
            result.setPageSize(pageSize);
            result.setPageIndex(pageIndex);

            LOG.debug(findCaller() + "|Query result：" + result.size() + "/" + result.getTotal() + " records.");
            return result;
        } catch (Exception e) {
            throw new DAOException("Query failed:", e, rangedSql == null ? sql : rangedSql, params);
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
    protected String getRangedSql(String sql, int startPos, int endPos) {
        return null;
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
        String countSql = getCountSql(sql);
        List<Row> list = query(null, countSql, params, -1, -1);
        if (!list.isEmpty()) {
            Row map = list.get(0);
            return map.getInteger("cnt", 0);
        } else {
            return 0;
        }
    }

    protected String getCountSql(String sql) {
        return "select count(*) cnt from (" + sql + ")";
    }

    public RowIterator queryIterator(String sql, List params) {
        printCommand(sql, params);
        try {
            executeQuery(sql, params);
        } catch (SQLException e) {
            throw new DAOException("Query failed:", e, sql, params);
        }
        return new RowIterator(rs);
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
    public List query(Class clazz, String sql, List params, int startPosition, int endPosition) {
        String rangedSql = endPosition <= 0 ?
                null : getRangedSql(sql, startPosition, endPosition);

        if (rangedSql != null) {
            printCommand(rangedSql, params);
        } else {
            printCommand(sql, params);
        }

        try {
            executeQuery(rangedSql == null ? sql : rangedSql, params);

            List<Object> result;
            if (rangedSql != null) {
                result = ResultSetUtil.readResultSet(rs, clazz, -1, -1);
            } else {
                result = ResultSetUtil.readResultSet(rs, clazz, startPosition, endPosition);
            }

            LOG.debug(findCaller() + "|Query result: " + result.size() + " records.");
            return result;
        } catch (Exception e) {
            throw new DAOException("Query failed:", e, rangedSql == null ? sql : rangedSql, params);
        } finally {
            closeButConnection();
        }
    }

    @SuppressWarnings({"unchecked"})
    public <T> T find(Class<T> wrapperClass, Object key, String tableName) {
        try {
            Command command = QueryCommandBuilder.buildByKey(connection, tableName, key);
            List list = query(wrapperClass, command.getStatement(), command.getParams(), 0, 1);
            return (T) (list.isEmpty() ? null : list.get(0));
        } catch (SQLException e) {
            throw new DAOException("Query failed:", e);
        }
    }

    public boolean exists(Object obj, String tableName) {
        try {
            Command command = QueryCommandBuilder.build(connection, tableName, obj);
            return !query(null, command.getStatement(), command.getParams(), -1, -1).isEmpty();
        } catch (SQLException e) {
            throw new DAOException("Delete failed: " + e.getMessage(), e);
        }
    }

    // 执行语句并将结果赋值给 this.rs
    private void executeQuery(String sql, List params) throws SQLException {

        // PreparerdStatement 可以不用就不用，以免占用过多 Oracle 的指针。
        if (params == null || params.isEmpty()) {
            st = createNormalStatement();
            if (TIMEOUT != -1) {
                st.setQueryTimeout(TIMEOUT);
            }
            rs = st.executeQuery(sql);
        } else {
            PreparedStatement ps = createPreparedStatement(sql);
            st = ps;
            insertParams(params);
            if (TIMEOUT != -1) {
                ps.setQueryTimeout(TIMEOUT);
            }
            rs = ps.executeQuery();
        }
    }

    ////////////////////////////////////////////////////////////////

    public int execute(BatchCommand command) {

        if (command == BatchCommand.EMPTY) {
            return 0;
        }

        printBatchCommand(command);
        try {
            // 执行语句
            List<List<Object>> params = command.getParams();
            PreparedStatement ps = createPreparedStatement(command.getCommand());
            st = ps;

            for (List param : params) {
                insertBatchParams(command, param);
                ps.addBatch();
            }

            int counter = 0;
            int[] counts = ps.executeBatch();
            for (int count : counts) {
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

    /**
     * 为批处理命令填入参数值
     *
     * @param command 批处理命令
     * @param params  参数
     *
     * @throws SQLException 如果填入参数失败
     */
    private void insertBatchParams(BatchCommand command, List params) throws SQLException {
        int length = StringUtil.count(command.getCommand(), "\\?");

        List<Integer> paramTypes = new ArrayList<Integer>();
        if (command.getColumnInfos() != null) {
            for (int i = 0; i < length; i++) {
                paramTypes.add(command.getColumnInfos()[i].getDataType());
            }
        }

        insertParams(params, paramTypes);
    }

    public int execute(String sql, List params) {
        return execute(sql, params, null);
    }

    public int execute(String sql, List params, List paramTypes) throws DAOException {
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
                PreparedStatement ps = createPreparedStatement(sql);
                st = ps;
                insertParams(params, paramTypes);
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

    // 为普通 SQL 语句填入参数值
    private void insertParams(List params) throws SQLException {
        insertParams(params, null);
    }

    /**
     * 填入参数值
     *
     * @param params     参数值。元素的个数要和语句中的 ? 数量一致。
     * @param paramTypes 参数值对应的 SQL 类型，每个元素都是 Integer 对象。如果没有则传 null。
     *
     * @throws SQLException 如果插入参数失败
     */
    private void insertParams(List params, List paramTypes) throws SQLException {
        PreparedStatement ps = (PreparedStatement) st;
        for (int i = 0; i < params.size(); i++) {
            Object value = params.get(i);
            if (value != null) {
                value = TypeUtil.cconvertParamValue(value);
                ps.setObject(i + 1, value);
            } else {
                if (paramTypes != null && paramTypes.size() > i) {
                    ps.setNull(i + 1, (Integer) paramTypes.get(i));
                } else {
                    ps.setObject(i + 1, null); // this will cause exception
                }
            }
        }
    }

    public void insert(Object object, String tableName) {
        Command command = new Command();
        try {
            command = InsertCommandBuilder.build(connection, tableName, object);
            execute(command.getStatement(), command.getParams());
        } catch (SQLException e) {
            throw new DAOException("Execution failed: " + e.getMessage(), e, command.getStatement(), command.getParams());
        }
    }

    public void insertList(List list, String table) {
        try {
            execute(InsertCommandBuilder.buildBatch(connection, table, list));
        } catch (SQLException e) {
            throw new DAOException("Insert Failed: " + e.getMessage(), e);
        }
    }

    public void insertMap(Map row, String tableName) {
        insert(row, tableName);
    }

    public int delete(Object obj, String tableName) {
        Command command = new Command();
        try {
            command = DeleteCommandBuilder.build(connection, tableName, obj);
            return execute(command.getStatement(), command.getParams());
        } catch (SQLException e) {
            throw new DAOException("Delete failed: " + e.getMessage(), e, command.getStatement(), command.getParams());
        }
    }

    public int deleteByKey(Object key, String tableName) {
        try {
            Command command = DeleteCommandBuilder.buildByKey(connection, tableName, key);
            return execute(command.getStatement(), command.getParams());
        } catch (SQLException e) {
            throw new DAOException("Delete failed: " + e.getMessage(), e);
        }
    }

    ////////////////////////////////////////////////////////////////

    public List call(String name, Object[] params) {
        try {
            SpParam[] spParams = StorageProsedureHelper.createSpParams(name, params, connection);
            LOG.debug(findCaller() + "(procedure)" + name + Arrays.asList(spParams));
            CallableStatement cs = StorageProsedureHelper.createCallableStatement(name, spParams, connection);
            if (TIMEOUT != -1) {
                cs.setQueryTimeout(TIMEOUT);
            }
            cs.executeQuery();
            return readResult(spParams, cs);
        } catch (IOException e) {
            throw new DAOException("Procedure failed: " + e.getMessage(), e, name, Arrays.asList(params));
        } catch (SQLException e) {
            throw new DAOException("Procedure failed: " + e.getMessage(), e, name, Arrays.asList(params));
        }
    }

    public List callFunction(String name, Object[] params) {

        try {
            if (!ConnectionUtil.isOracle(connection)) {
                throw new DAOException("Target database is not oracle.");
            }

            LOG.debug(findCaller() + "(function)" + name + Arrays.asList(params));
            SpParam[] spParams = FunctionHelper.createFunctionParams(name, params, connection);
            int resultType = spParams[0].getSqlType();

            // 去掉第一个
            spParams = (SpParam[]) ArrayUtils.subarray(spParams, 1, spParams.length);

            CallableStatement cs = FunctionHelper.createCallableStatement(name, resultType, spParams, connection);
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
        SpParam[] outParams = new SpParam[params.length + 1];
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
     *
     * @throws SQLException        如果读取结果失败
     * @throws java.io.IOException 如果读取 LOB 对象失败
     */
    private List readResult(SpParam[] params, CallableStatement cs) throws SQLException, IOException {
        List<Object> result = new ArrayList<Object>();
        try {
            for (int i = 0; i < params.length; i++) {
                SpParam param = params[i];
                if (param.getType() == SpParamType.OUT || param.getType() == SpParamType.IN_OUT) {
                    Object value = cs.getObject(i + 1);

                    // 对 Oracle 的 CURSOR 类型进行特殊处理
                    if (ConnectionUtil.isOracle(connection) && param.getSqlType() == OracleTypes.CURSOR) {
                        ResultSet rs1 = (ResultSet) value;
                        result.add(ResultSetUtil.readResultSet(rs1, null, -1, -1));

                    } else {
                        result.add(TypeUtil.convertDatabaseValue(param.getSqlType(), value));
                    }
                }
            }
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
        // 这两个参数的选择是有讲究的，参见：
        // http://download.oracle.com/docs/cd/B10500_01/java.920/a96654/resltset.htm#1023726
        return connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
    }

    private PreparedStatement createPreparedStatement(String sql) throws SQLException {
        return connection.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
    }

    ////////////////////////////////////////////////////////////////

    private void printCommand(String sql, List params) {
        info.setLastCommand(sql);
        info.setLastExecuteTime(new java.util.Date());

        LOG.debug(findCaller() + "(" + info.getDsName() + "): " +
                sql.replaceAll("\n", " ") + " " + (params == null ? "" : params.toString()));
    }

    private String findCaller() {
        StackTraceElement[] traceElements = Thread.currentThread().getStackTrace();
        boolean daostarted = false;
        for (StackTraceElement traceElement : traceElements) {
            String className = traceElement.getClassName();
            int lineNumber = traceElement.getLineNumber();

            if (!daostarted) {
                if (className.startsWith("com.hyd.dao.")) {
                    daostarted = true;
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
        String sql = StringUtil.n(command.getCommand());
        info.setLastCommand(sql);
        info.setLastExecuteTime(new java.util.Date());

        List<List<Object>> params = command.getParams() == null ? new ArrayList<List<Object>>() : command.getParams();

        if (BATCH_LOG.isEnabled(Logger.Level.Debug)) {
            BATCH_LOG.debug("Batch(" + info.getDsName() + "):" + sql.replaceAll("\n", " ") + "; parameters:");
            for (List<Object> param : params) {
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
            if (!connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            LOG.error("", e);
        }
    }

    public void close() {
        if (connection != null) {
            try {
                if (!connection.getAutoCommit()) {
                    connection.commit();
                }
            } catch (SQLException e) {
                LOG.error("", e);
            }

            closeConnection();
        }
        info.setClosed(true);
    }

    public boolean isClosed() {
        try {
            return connection == null || connection.isClosed();
        } catch (SQLException e) {
            LOG.error("Error checking connection: " + e.getMessage(), e);
            return true;
        }
    }

    public void rollbackAndClose() {
        if (connection != null) {
            try {
                if (!connection.getAutoCommit()) {
                    connection.rollback();
                }
            } catch (SQLException e) {
                LOG.error("", e);
            }
            closeConnection();
        }
        info.setClosed(true);
    }

}

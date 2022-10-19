package com.hyd.dao.sp;

import com.hyd.dao.DAOException;
import com.hyd.dao.Row;
import com.hyd.dao.mate.util.ResultSetUtil;
import com.hyd.dao.mate.util.Str;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 执行存储过程帮助类
 */
public final class StorageProcedureHelper {

    public static final Map<Integer, SpParamType> SP_PARAM_TYPES = new HashMap<>();

    static {
        SP_PARAM_TYPES.put(1, SpParamType.IN);
        SP_PARAM_TYPES.put(2, SpParamType.IN_OUT);
        SP_PARAM_TYPES.put(4, SpParamType.OUT);
    }

    private StorageProcedureHelper() {

    }

    /**
     * 创建一个 CallableStatement
     *
     * @param name       存储过程名
     * @param params     调用参数
     * @param connection 数据库连接
     *
     * @return CallableStatement 对象
     * @throws SQLException 如果创建失败
     */
    public static CallableStatement createCallableStatement(
        String name, SpParam[] params, Connection connection) throws SQLException {
        var call_str = generateCallStatement(name, params);
        var cs = connection.prepareCall(call_str);
        setupParams(params, cs);
        return cs;
    }

    /**
     * 设置存储过程的调用参数，以及注册返回值
     *
     * @param params 参数
     * @param cs     要执行的 CallableStatement
     *
     * @throws SQLException 如果设置参数失败
     */
    private static void setupParams(SpParam[] params, CallableStatement cs) throws SQLException {
        for (var i = 0; i < params.length; i++) {
            var param = params[i];
            if ((param.getType() == SpParamType.IN || param.getType() == SpParamType.IN_OUT)
                && param.getValue() != null) {
                cs.setObject(i + 1, param.getValue());
            }
            if (param.getType() == SpParamType.OUT || param.getType() == SpParamType.IN_OUT) {
                if (Str.isEmptyString(param.getName())) {
                    cs.registerOutParameter(i + 1, param.getSqlType());
                } else {
                    cs.registerOutParameter(i + 1, param.getSqlType(), param.getName());
                }
            }
        }
    }

    /**
     * 生成一个调用存储过程的语句，格式类似于“{call XXX(?,?,?)}”
     *
     * @param name   存储过程名
     * @param params 参数
     *
     * @return 调用存储过程的语句
     */
    private static String generateCallStatement(String name, SpParam... params) {
        return "{call " + name + "(" +
            Stream.of(params).map(p -> "?").collect(Collectors.joining(",")) +
            ")}";
    }

    /**
     * 创建存储过程调用参数
     *
     * @param name   存储过程名称
     * @param params 参数值
     * @param conn   数据库连接（执行完后不会关闭）
     *
     * @return 存储过程调用参数
     * @throws SQLException 如果获取存储过程信息失败
     */
    public static SpParam[] createSpParams(String name, Object[] params, Connection conn) throws SQLException {
        try {
            var rows = getSpParamDefinitions(conn, name);

            var sp_params = new SpParam[rows.size()];
            var param_counter = 0;

            for (var i = 0; i < rows.size(); i++) {
                var row = rows.get(i);
                var data_type = getIntegerValue(row, "data_type");
                var column_type = getIntegerValue(row, "column_type");

                var type = SP_PARAM_TYPES.get(column_type);
                Object value;
                if (type != SpParamType.OUT) {
                    value = params[param_counter];
                    param_counter++;
                } else {
                    value = null;
                }
                sp_params[i] = new SpParam(type, data_type, value);
            }

            return sp_params;
        } catch (SQLException e) {
            throw e;
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }

    private static int getIntegerValue(HashMap row, String colName) {
        var dataType = row.get(colName);

        if (dataType != null) {
            return new BigDecimal(dataType.toString()).intValue();
        } else {
            throw new DAOException("Unknown procedure parameter type: " + row);
        }
    }

    /**
     * 查询存储过程参数信息
     *
     * @param conn   数据库连接
     * @param spName 存储过程名称
     *
     * @return 参数信息
     * @throws Exception 如果获取存储过程信息失败
     */
    private static List<Row> getSpParamDefinitions(Connection conn, String spName) throws Exception {
        var metaData = conn.getMetaData();

        String schema;
        var fixedSpName = spName;

        if (!spName.contains(".")) {
            schema = metaData.getUserName().toUpperCase();
        } else {
            schema = spName.split("\\.")[0].toUpperCase();
            fixedSpName = spName.substring(spName.lastIndexOf(".") + 1);
        }

        try (var procedures = metaData.getProcedureColumns(null, schema, fixedSpName.toUpperCase(), "%")) {
            var rows = ResultSetUtil.readResultSet(procedures);
            rows.sort(Comparator.comparing(m -> m.getIntegerObject("sequence")));
            return rows;
        }
    }
}

package com.hyd.dao.database.function;

import com.hyd.dao.DAOException;
import com.hyd.dao.mate.util.ResultSetUtil;
import com.hyd.dao.sp.SpParam;
import com.hyd.dao.sp.SpParamType;
import com.hyd.dao.sp.StorageProcedureHelper;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 数据库 function 处理帮助类
 */
public class FunctionHelper {

    public static CallableStatement createCallableStatement(
            String name, int resultType, SpParam[] params, Connection connection) throws SQLException {
        var call_str = generateFunctionStatement(name, params);
        var cs = connection.prepareCall(call_str);
        setupFunctionParams(params, resultType, cs);
        return cs;
    }

    private static void setupFunctionParams(SpParam[] params, int resultType, CallableStatement cs) throws SQLException {
        cs.registerOutParameter(1, resultType);
        for (var i = 0; i < params.length; i++) {
            var param = params[i];
            if (param.getType() == SpParamType.IN || param.getType() == SpParamType.IN_OUT) {
                cs.setObject(i + 2, param.getValue());
            }
            if (param.getType() == SpParamType.OUT || param.getType() == SpParamType.IN_OUT) {
                cs.registerOutParameter(i + 2, param.getSqlType());
            }
        }
    }

    private static String generateFunctionStatement(String name, SpParam[] params) {
        return "{? = call " + name + "(" +
                Stream.of(params).map(p -> "?").collect(Collectors.joining(",")) +
                ")}";
    }

    public static SpParam[] createFunctionParams(String name, Object[] params, Connection connection) throws Exception {
        var metaData = connection.getMetaData();

        var schema = name.contains(".") ?
                name.split("\\.")[0].toUpperCase() : metaData.getUserName().toUpperCase();

        var lastPartOfName = name.contains(".") ? name.substring(name.lastIndexOf(".") + 1) : name;

        var rs = metaData.getProcedureColumns(null, schema, lastPartOfName.toUpperCase(), "%");
        var functionColumns = ResultSetUtil.readResultSet(rs);

        if (functionColumns.isEmpty()) {
            throw new DAOException("存储过程 " + name + " 没有找到任何参数。");
        }

        // 按照 sequence 的值对 map 数组进行排序
        functionColumns.sort(Comparator.comparing(m -> m.getIntegerObject("sequence")));

        var result = new SpParam[functionColumns.size()];

        for (var i = 0; i < functionColumns.size(); i++) {
            var row = functionColumns.get(i);
            // function_columns 的第一行是方法的返回值
            // 注意，params 的长度可能小于 function 参数列表的长度，这时候假设多余的参数是有缺省值的。
            var param_value = (i > 0 && i <= params.length) ? params[i - 1] : null;
            result[i] = createSpParam(row, param_value);
        }

        return result;
    }

    private static SpParam createSpParam(HashMap row, Object param_value) {
        var param_type = StorageProcedureHelper.SP_PARAM_TYPES.get(((Double) row.get("column_type")).intValue());
        var data_type = ((Double) row.get("data_type")).intValue();
        return new SpParam(param_type, data_type, param_value);
    }

}

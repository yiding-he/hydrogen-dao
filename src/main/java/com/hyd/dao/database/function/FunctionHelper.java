package com.hyd.dao.database.function;

import com.hyd.dao.DAOException;
import com.hyd.dao.sp.SpParam;
import com.hyd.dao.sp.SpParamType;
import com.hyd.dao.sp.StorageProsedureHelper;
import com.hyd.dao.util.BeanUtil;
import com.hyd.dao.util.ResultSetUtil;

import java.sql.*;
import java.util.HashMap;

/**
 * 数据库 function 处理帮助类
 */
public class FunctionHelper {

    public static CallableStatement createCallableStatement(
            String name, int resultType, SpParam[] params, Connection connection) throws SQLException {
        String call_str = generateFunctionStatement(name, params);
        CallableStatement cs = connection.prepareCall(call_str);
        setupFunctionParams(params, resultType, cs);
        return cs;
    }

    private static void setupFunctionParams(SpParam[] params, int resultType, CallableStatement cs) throws SQLException {
        cs.registerOutParameter(1, resultType);
        for (int i = 0; i < params.length; i++) {
            SpParam param = params[i];
            if (param.getType() == SpParamType.IN || param.getType() == SpParamType.IN_OUT) {
                cs.setObject(i + 2, param.getValue());
            }
            if (param.getType() == SpParamType.OUT || param.getType() == SpParamType.IN_OUT) {
                cs.registerOutParameter(i + 2, param.getSqlType());
            }
        }
    }

    private static String generateFunctionStatement(String name, SpParam[] params) {
        String call_str = "{? = call " + name + "(";
        for (int i = 0; i < params.length; i++) {
            call_str += "?" + (i == params.length - 1 ? "" : ",");
        }
        call_str += ")}";
        return call_str;
    }

    public static SpParam[] createFunctionParams(String name, Object[] params, Connection connection) throws Exception {
        DatabaseMetaData metaData = connection.getMetaData();

        String schema = name.contains(".") ?
                name.split("\\.")[0].toUpperCase() : metaData.getUserName().toUpperCase();

        if (name.contains(".")) {
            name = name.substring(name.lastIndexOf(".") + 1);
        }

        ResultSet rs = metaData.getProcedureColumns(null, schema, name.toUpperCase(), "%");
        HashMap[] function_columns = ResultSetUtil.readResultSet(rs);

        if (function_columns.length == 0) {
            throw new DAOException("存储过程 " + name + " 没有找到任何参数。");
        }

        // 按照 sequence 的值对 map 数组进行排序
        BeanUtil.sort(function_columns, "sequence");

        SpParam[] result = new SpParam[function_columns.length];

        for (int i = 0; i < function_columns.length; i++) {
            HashMap row = function_columns[i];
            // function_columns 的第一行是方法的返回值
            // 注意，params 的长度可能小于 function 参数列表的长度，这时候假设多余的参数是有缺省值的。 
            Object param_value = (i > 0 && i <= params.length) ? params[i - 1] : null;
            result[i] = createSpParam(row, param_value);
        }
        return result;
    }

    private static SpParam createSpParam(HashMap row, Object param_value) {
        SpParamType param_type = StorageProsedureHelper.sp_param_types.get(((Double) row.get("column_type")).intValue());
        int data_type = ((Double) row.get("data_type")).intValue();
        return new SpParam(param_type, data_type, param_value);
    }

}

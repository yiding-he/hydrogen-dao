package com.hyd.dao.sql;

import com.hyd.dao.database.commandbuilder.Command;

import java.util.List;

/**
 * 帮助生成查询语句的类
 *
 * @author yiding.he
 */
public class SqlHelper {

    /**
     * 构造一个 in 条件。例如
     * in("role", [1,2,3]) => Command{"(role in (?,?,?))", [1,2,3]}
     * in("role not", [1,2,3]) => Command{"(role not in (?,?,?))", [1,2,3]}
     *
     * @param columnName 字段名
     * @param values     值
     *
     * @return in 条件语句
     */
    public static Command in(String columnName, List<Object> values) {
        StringBuilder sb = new StringBuilder("(" + columnName + " in (");

        for (int i = 1; i <= values.size(); i++) {
            sb.append("?,");
        }

        if (sb.charAt(sb.length() - 1) == ',') {
            sb.deleteCharAt(sb.length() - 1);
        }

        return new Command(sb.toString(), values);
    }
}

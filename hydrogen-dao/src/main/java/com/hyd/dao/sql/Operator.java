package com.hyd.dao.sql;

import com.hyd.dao.SQL;
import lombok.Getter;

@Getter
public enum Operator {

    eq("="),

    neq("!="),

    gt(">"),

    gte(">="),

    lt("<"),

    lte("<="),

    like("LIKE"),

    in("IN"),

    nin("NOT IN"),

    isnull("IS NULL"),

    notnull("IS NOT NULL");

    private final String code;

    Operator(String code) {
        this.code = code;
    }

    /**
     * 将当前操作对应的字段和参数值注入到动态 SQL 对象中。
     * 如果当前操作只支持单个参数，则只会取第一个参数注入。
     */
    public <G extends SQL.Generatable<G>> void inject(G sql, String columnName, Object... values) {
        if (values == null || values.length == 0) {
            throw new IllegalArgumentException(this.code + " 操作没有参数");
        }
        if (this == in || this == nin) {
            sql.And(columnName + " " + this.code + " ?", values);
        } else if (this == isnull || this == notnull) {
            sql.And(columnName + " " + this.code);
        } else {
            var firstValue = values[0];
            sql.And(columnName + " " + this.code + " ?", firstValue);
        }
    }

    public static Operator of(String code) {
        for (Operator operator : values()) {
            if (operator.code.equals(code)) {
                return operator;
            }
        }
        throw new IllegalArgumentException("不支持的操作符：" + code);
    }
}

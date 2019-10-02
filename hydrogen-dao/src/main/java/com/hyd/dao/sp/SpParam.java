package com.hyd.dao.sp;

/**
 * 存储过程和 function 的调用参数
 */

public class SpParam {

    private SpParamType type;   // 参数的输入/输出类型

    private String name;        // 自定义的 Oracle 数据类型名称

    private int sqlType;        // 参数的数据类型。参考 java.sql.Types

    private Object value;       // 参数值。如果参数不是输入参数，则 value 的值被忽略。

    public SpParam(SpParamType type, int sqlType, Object value) {
        this.type = type;
        this.sqlType = sqlType;
        this.value = value;
    }

    public SpParam(SpParamType type, String name, int sqlType, Object value) {
        this.type = type;
        this.name = name;
        this.sqlType = sqlType;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SpParamType getType() {
        return type;
    }

    public int getSqlType() {
        return sqlType;
    }

    public void setSqlType(int sqlType) {
        this.sqlType = sqlType;
    }

    public void setType(SpParamType type) {
        this.type = type;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String toString() {
        return "{" + getType() + ":" + getValue() + "}";
    }
}

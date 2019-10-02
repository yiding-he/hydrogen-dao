package com.hyd.dao.sp;

/**
 * 存储过程参数类型
 */

public class SpParamType {

    private String name;

    /**
     * 表示该参数是输入参数
     */
    public static final SpParamType IN = new SpParamType("in");

    /**
     * 表示该参数是输出参数
     */
    public static final SpParamType OUT = new SpParamType("out");

    /**
     * 表示该参数既是输入参数也是输出参数
     */
    public static final SpParamType IN_OUT = new SpParamType("in_out");

    public SpParamType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toString() {
        return "SpParamType." + name;
    }
}

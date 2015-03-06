package com.hyd.dao.database.commandbuilder;

import java.util.List;

/**
 * 表示 SQL 命令的类。其中包含 SQL 语句和参数两个部分。参数的值要和 SQL 语句中的问号一一对应。
 */
public class Command {

    private String statement;

    private List params;

    private List paramTypes;

    /**
     * 缺省构造函数
     */
    public Command() {
    }

    /**
     * 构造函数
     *
     * @param statement SQL 语句
     * @param params    参数
     */
    public Command(String statement, List params) {
        this.statement = statement;
        this.params = params;
    }

    public Command(String statement, List params, List paramTypes) {
        this.statement = statement;
        this.params = params;
        this.paramTypes = paramTypes;
    }

    public List getParamTypes() {
        return paramTypes;
    }

    public void setParamTypes(List paramTypes) {
        this.paramTypes = paramTypes;
    }

    /**
     * 获得 SQL 语句
     *
     * @return SQL 语句
     */
    public String getStatement() {
        return statement;
    }

    /**
     * 设置 SQL 语句
     *
     * @param statement SQL 语句
     */
    public void setStatement(String statement) {
        this.statement = statement;
    }

    /**
     * 获得参数
     *
     * @return 参数
     */
    public List getParams() {
        return params;
    }

    /**
     * 设置参数
     *
     * @param params 参数
     */
    public void setParams(List params) {
        this.params = params;
    }

    @Override
    public String toString() {
        return "Command{" +
                "statement='" + statement + '\'' +
                ", params=" + params +
                '}';
    }
}

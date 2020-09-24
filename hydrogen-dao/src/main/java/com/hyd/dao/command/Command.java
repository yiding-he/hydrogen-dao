package com.hyd.dao.command;

import java.util.List;

/**
 * 对 PreparedStatement 语句和参数的封装
 */
public class Command {

    /**
     * 包含 "?" 参数占位符的 SQL 语句
     */
    private String statement;

    /**
     * 与参数占位符对应的参数值列表
     */
    private List<Object> params;

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
    public Command(String statement, List<Object> params) {
        this.statement = statement;
        this.params = params;
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
    public List<Object> getParams() {
        return params;
    }

    /**
     * 设置参数
     *
     * @param params 参数
     */
    public void setParams(List<Object> params) {
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

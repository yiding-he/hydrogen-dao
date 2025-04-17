package com.hyd.dao.command;

import com.hyd.dao.mate.util.Str;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * 对 PreparedStatement 语句和参数的封装
 */
@Setter
@Getter
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
     * 默认构造函数
     */
    public Command() {
        this.statement = "";
        this.params = new ArrayList<>();
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

    public Command(String statement) {
        this.statement = statement;
        this.params = new ArrayList<>();
    }

    public Command append(Command other) {
        return append(other.statement, other.params);
    }

    public Command append(String statement) {
        return append(statement, new ArrayList<>());
    }

    public Command append(String statement, List<Object> params) {
        // 如果两边都不为空则需要进行整理：
        // 1. 对于前面的语句，去掉首尾空白字符，避免出现多个空格；
        // 2. 对于后面的语句，加上一个空格前缀，避免粘在一起。
        if (!Str.isEmptyString(this.statement) && !Str.isEmptyString(statement)) {
            this.statement = this.statement.trim();
            statement = " " + statement;
        }
        this.statement += statement;
        this.params.addAll(params);
        return this;
    }

    public Command removeSuffix(String suffix) {
        if (statement.endsWith(suffix)) {
            statement = statement.substring(0, statement.length() - suffix.length());
        }
        return this;
    }

    @Override
    public String toString() {
        return "Command{" +
                "statement='" + statement + '\'' +
                ", params=" + params +
                '}';
    }
}

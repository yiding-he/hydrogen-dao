package com.hyd.dao.sql;

import com.hyd.dao.database.commandbuilder.Command;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 一个简单的生成查询语句的类
 *
 * @author yiding.he
 */
public class SimpleSqlBuilder {

    private List<Connect> connects = new ArrayList<Connect>();

    private List<String> columns = new ArrayList<String>();

    private String condition;

    private String from;

    private String order;

    private List<Object> params;

    /**
     * 为两个表字段之间添加引用
     *
     * @param column1 第一个表的字段
     * @param column2 第二个表的字段
     *
     * @return this
     */
    public SimpleSqlBuilder connect(String column1, String column2) {
        connects.add(new Connect(column1, column2));
        return this;
    }

    /**
     * 设置查询结果的字段。每个参数值的格式为 "表明.字段名 [别名]"
     *
     * @param columns 查询结果的字段
     *
     * @return this
     */
    public SimpleSqlBuilder select(String... columns) {
        this.columns.addAll(Arrays.asList(columns));
        return this;
    }

    /**
     * 设置查询条件
     *
     * @param condition 查询条件（不含 where）
     * @param params    条件参数值
     *
     * @return this
     */
    public SimpleSqlBuilder filter(String condition, Object... params) {
        this.condition = condition;
        this.params = new ArrayList<Object>(Arrays.asList(params));
        return this;
    }

    /**
     * 设置查询的来源表
     *
     * @param tableNames 表名
     *
     * @return this
     */
    public SimpleSqlBuilder from(String... tableNames) {
        this.from = StringUtils.join(tableNames, ",");
        return this;
    }

    /**
     * 设置排序条件
     *
     * @param order 排序条件
     *
     * @return this
     */
    public SimpleSqlBuilder order(String order) {
        this.order = order;
        return this;
    }

    private String generateConnects() {
        String str = "";
        for (Connect connect : connects) {
            str += connect.toString() + " and ";
        }
        return StringUtils.removeEnd(str, " and ");
    }

    public Command build() {
        String sql = "select " + StringUtils.join(columns, ",")
                + " from " + this.from
                + " where " + generateConnects() + " and " + condition
                + " order by " + order;
        return new Command(sql, params);
    }

    /////////////////////////////////////////

    private class Connect {

        public String column1;

        public String column2;

        private Connect(String column1, String column2) {
            this.column1 = column1;
            this.column2 = column2;
        }

        @Override
        public String toString() {
            return column1 + "=" + column2;
        }
    }
}

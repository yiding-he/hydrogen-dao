package com.hyd.dao.sql;

import com.hyd.dao.SQL;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@Getter
public class Conditions extends HashMap<String, Object> {

    /**
     * 设置返回哪些字段，如果为空则返回全部字段
     */
    private final List<String> columns = new ArrayList<>();

    /**
     * 查询条件
     */
    private final List<Condition> conditions = new ArrayList<>();

    /**
     * 排序条件
     */
    private final List<String> orderBy = new ArrayList<>();

    /**
     * 分页查询时的每页记录数
     */
    @Setter
    private int pageSize = 10;

    /**
     * 分页查询时的当前页号，0表示第一页
     */
    @Setter
    private int pageIndex = 0;

    public Conditions addCondition(Condition condition) {
        this.conditions.add(condition);
        return this;
    }

    public boolean hasCondition() {
        return !this.conditions.isEmpty();
    }

    public Conditions addOrderBy(String orderBy) {
        this.orderBy.add(orderBy);
        return this;
    }

    public Conditions columns(String... columns) {
        this.columns.clear();
        Collections.addAll(this.columns, columns);
        return this;
    }

    /**
     * 将组合查询条件添加到 Select 对象中
     */
    public void inject(SQL.Select select) {
        if (!this.columns.isEmpty()) {
            select.Columns(this.columns.toArray(String[]::new));
        }
        for (Condition condition : conditions) {
            condition.getOperator().inject(
                select, condition.getColumnName(), condition.getValues().toArray()
            );
        }
    }
}

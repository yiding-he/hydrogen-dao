package com.hyd.dao;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * 分页查询结果
 */
@Getter
public class Page<T> {

    @Setter
    private int total;      // 总记录数

    @Setter
    private int pageIndex;

    @Setter
    private int pageSize;

    private List<T> list;

    public Page() {
    }

    public Page(Collection<? extends T> c) {
        this(c, 0, 0, 0);
    }

    public Page(int total, int pageIndex, int pageSize) {
        this(Collections.emptyList(), total, pageIndex, pageSize);
    }

    public Page(Collection<? extends T> c, int pageIndex, int pageSize) {
        this(c, c.size(), pageIndex, pageSize);
    }

    public Page(Collection<? extends T> c, int total, int pageIndex, int pageSize) {
        this.list = new ArrayList<>(c);
        this.total = total;
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
    }

    public int getTotalPage() {
        return this.pageSize == 0? 0: ((this.total + this.pageSize - 1) / this.pageSize);
    }

    public int size() {
        return this.list == null ? 0 : this.list.size();
    }

    public void addAll(Collection<T> collection) {
        if (list != null) {
            list.addAll(collection);
        } else {
            list = new ArrayList<>(collection);
        }
    }

    public boolean isEmpty() {
        return list == null || list.isEmpty();
    }

    public T get(int index) {
        return list == null ? null : list.get(index);
    }

}

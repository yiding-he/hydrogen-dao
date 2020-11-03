package com.hyd.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * 分页查询结果
 */
public class Page<T> {

    private int total;      // 总记录数

    private int pageIndex;

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

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
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

    public List<T> getList() {
        return list;
    }
}

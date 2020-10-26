package com.hyd.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * 分页查询结果
 */
public class Page<T> extends ArrayList<T> {

    private int total;      // 总记录数

    private int pageIndex;

    private int pageSize;

    public Page(int initialCapacity) {
        super(initialCapacity);
    }

    public Page() {
    }

    public Page(Collection<? extends T> c) {
        this(c, 0, 0, 0);
    }

    public Page(int total, int pageIndex, int pageSize) {
        this(Collections.emptyList(), total, pageIndex, pageSize);
    }

    public Page(Collection<? extends T> c, int total, int pageIndex, int pageSize) {
        super(c);
        this.total = total;
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
    }

    public Page(Collection<? extends T> c, int pageIndex, int pageSize) {
        super(c);
        this.total = c.size();
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
        return (this.total + this.pageSize - 1) / this.pageSize;
    }
}

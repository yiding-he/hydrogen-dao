package com.hyd.dao;

import java.util.ArrayList;

/**
 * 分页查询结果
 */
public class Page<T> extends ArrayList<T> {

    private int total;

    private int pageIndex;

    private int pageSize;

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
        return this.total + (this.pageSize - 1) / this.pageSize;
    }
}

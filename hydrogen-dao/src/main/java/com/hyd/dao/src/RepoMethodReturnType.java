package com.hyd.dao.src;

/**
 * (description)
 * created at 2018/4/18
 *
 * @author yidin
 */
public enum RepoMethodReturnType {

    Single("单条记录"), Collection("多条记录"), Page("分页结果")

    ///////////////////////////////////////////////
    ;

    private String name;

    RepoMethodReturnType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

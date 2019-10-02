package com.hyd.dao;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 对于 Oracle 数据库，标记为 @Sequencce 意味着该字段的值从 sequence 中获取；
 *
 * @author yiding.he
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Sequence {

    String sequenceName();
}

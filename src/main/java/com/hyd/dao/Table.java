package com.hyd.dao;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 标识类对应的表明
 *
 * @author yiding.he
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Table {

    String name();
}

package com.hyd.dao.src.fx;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * (description)
 * created at 2018/4/10
 *
 * @author yidin
 */
@FunctionalInterface
public interface ConnectionFactory {

    Connection getConnection() throws SQLException;
}

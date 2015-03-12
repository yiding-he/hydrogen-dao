package com.hyd.dao;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 对 {@link java.sql.Connection} 对象进行操作的接口。实现这个接口时无需手动关闭 Connection 对象。
 * created at 2014/12/25
 *
 * @author Yiding
 */
public interface ConnectionExecutor {

    void execute(Connection connection) throws SQLException;
}

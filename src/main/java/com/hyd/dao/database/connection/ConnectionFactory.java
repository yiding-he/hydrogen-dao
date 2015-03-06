package com.hyd.dao.database.connection;

import com.hyd.dao.DAOException;

import java.sql.Connection;

/**
 * 提供数据库连接的工厂类
 *
 * @author <a href="mailto:yiding.he@gmail.com">yiding_he</a>
 */
public abstract class ConnectionFactory {

    private String dsName;

    private boolean available = true;

    private com.hyd.dao.config.Connection config;

    protected ConnectionFactory(String dsName, com.hyd.dao.config.Connection config) {
        this.dsName = dsName;
        this.config = config;
    }

    public com.hyd.dao.config.Connection getConfig() {
        return config;
    }

    /**
     * 获得是否是缺省的连接配置
     *
     * @return 是否是缺省的连接配置
     */
    public boolean isDefaultFactory() {
        return getConfig().isDefaultConfig();
    }

    /**
     * 获得数据源名称
     *
     * @return 数据源名称
     */
    public String getDsName() {
        return dsName;
    }

    /**
     * 设置数据源名称
     *
     * @param dsName 数据源名称
     */
    public void setDsName(String dsName) {
        this.dsName = dsName;
    }

    /**
     * 获取连接工厂是否可用
     *
     * @return 连接工厂是否可用
     */
    public boolean isAvailable() {
        return available;
    }

    /**
     * 设置连接工厂是否可用
     *
     * @param available 连接工厂是否可用
     */
    public void setAvailable(boolean available) {
        this.available = available;
    }

    /**
     * 获得一个可用的连接（仅当使用本地连接池时有效）
     *
     * @return 一个可用的连接
     *
     * @throws DAOException 如果获取连接失败
     */
    public abstract Connection getConnection() throws DAOException;

    /**
     * 获得当前活动的连接数（仅当使用本地连接池时有效）
     *
     * @return 当前活动的连接数。-1 表示无法获取。
     */
    public int active() {
        return -1;
    }

    /**
     * 获得最大允许的活动连接数（仅当使用本地连接池时有效）
     *
     * @return 最大允许的活动连接数。-1 表示无法获取。
     */
    public int max() {
        return -1;
    }
}

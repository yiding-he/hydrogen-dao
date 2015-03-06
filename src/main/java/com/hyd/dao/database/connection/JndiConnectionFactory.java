package com.hyd.dao.database.connection;

import com.hyd.dao.DAOException;
import com.hyd.dao.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Properties;


/**
 * JNDI 连接工厂
 *
 * @author <a href="mailto:yiding.he@gmail.com">yiding_he</a>
 */
public class JndiConnectionFactory extends ConnectionFactory {

    static final Logger log = LoggerFactory.getLogger(JndiConnectionFactory.class);

    private static HashMap<String, DataSource> sourceCache = new HashMap<String, DataSource>();

    public JndiConnectionFactory(String dsName, com.hyd.dao.config.Connection config) {
        super(dsName, config);
    }

    public Connection getConnection() throws DAOException {
        com.hyd.dao.config.Connection config = getConfig();
        Connection connection;
        String jndi = config.get("jndiname");
        String driver = config.get("driver");
        String server = config.get("server");
        DataSource source = sourceCache.get(jndi);
        try {
            if (source != null) {
                try {
                    connection = source.getConnection();
                } catch (SQLException e) {
                    // 重新获取数据源
                    source = lookupSource(jndi, driver, server);
                    sourceCache.put(jndi, source);
                    connection = source.getConnection();
                }
            } else {
                source = lookupSource(jndi, driver, server);
                sourceCache.put(jndi, source);
                connection = source.getConnection();
            }
            log.debug("通过 JNDI 获取数据库连接：" + connection.getMetaData().getURL() + ":" +
                    connection.getMetaData().getUserName());
        } catch (Exception e) {
            throw new DAOException("connect to database server failed", e);
        }
        return connection;
    }

    /**
     * 获取数据源
     *
     * @param jndi   JNDI 名称
     * @param driver 工厂类的名称
     * @param server JNDI 地址(URL)
     *
     * @return 数据源
     *
     * @throws DAOException 如果获取数据源失败
     */
    private DataSource lookupSource(String jndi, String driver, String server) throws DAOException {
        log.debug("通过 JNDI 获得数据源：server=" + server + ", jndi=" + jndi);
        try {
            Context ctx;

            // 如果没有在 dao-config.xml 中指定 jndi 配置
            if (StringUtil.isEmptyString(driver) ||
                    StringUtil.isEmptyString(server)) {
                ctx = new InitialContext();
            } else {
                Properties p = new Properties();
                if (!StringUtil.isEmptyString(driver)) {
                    p.put(Context.INITIAL_CONTEXT_FACTORY, driver);
                }
                if (!StringUtil.isEmptyString(server)) {
                    p.put(Context.PROVIDER_URL, server);
                }
                ctx = new InitialContext(p);
            }

            return (DataSource) (ctx.lookup(jndi));
        } catch (NamingException e) {
            throw new DAOException(e);
        }

    }

}

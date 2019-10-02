package com.hyd.dao.src.fx;

import com.hyd.dao.log.Logger;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * (description)
 * created at 2018/4/10
 *
 * @author yidin
 */
public class ConnectionManager {

    private static final Logger LOG = Logger.getLogger(ConnectionManager.class);

    private ConnectionFactory connectionFactory;

    private Connection currentConnection;

    public ConnectionManager(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public void close() {
        try {
            if (currentConnection != null) {
                currentConnection.close();
                currentConnection = null;
            }
        } catch (SQLException e) {
            LOG.error("", e);
        }
    }

    public void withConnection(ConnectionExecutor executor) {
        try {
            if (currentConnection == null) {
                currentConnection = connectionFactory.getConnection();
            }

            executor.apply(currentConnection);
        } catch (Exception e) {
            Fx.error(e);
            close();
        }
    }
}

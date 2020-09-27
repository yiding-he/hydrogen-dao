package com.hyd.dao.database;

import com.hyd.dao.DAO;
import com.hyd.dao.database.executor.DefaultExecutor;
import com.hyd.dao.database.executor.Executor;
import com.hyd.dao.database.type.NameConverter;
import com.hyd.dao.mate.util.ConnectionContext;
import com.hyd.dao.transaction.TransactionManager;

/**
 * 构造 Executor 对象的工厂。
 *
 * @author <a href="mailto:yiding.he@gmail.com">yiding_he</a>
 */
public class ExecutorFactory {

    public static Executor getExecutor(DAO dao) {
        ConnectionContext context = TransactionManager.getConnectionContext(dao);
        NameConverter converter = dao.getNameConverter();
        return new DefaultExecutor(context, converter);
    }
}

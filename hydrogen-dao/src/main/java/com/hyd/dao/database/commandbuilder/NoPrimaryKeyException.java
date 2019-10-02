package com.hyd.dao.database.commandbuilder;

import com.hyd.dao.DAOException;

/**
 * 表示没有找到主键的异常
 */
public class NoPrimaryKeyException extends DAOException {

    public NoPrimaryKeyException() {
    }

    public NoPrimaryKeyException(String message) {
        super(message);
    }

    public NoPrimaryKeyException(Throwable cause) {
        super(cause);
    }

    public NoPrimaryKeyException(String message, Throwable cause) {
        super(message, cause);
    }
}

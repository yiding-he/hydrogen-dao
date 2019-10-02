package com.hyd.dao;

/**
 * 与事务处理失败有关的异常
 */
public class TransactionException extends DAOException {

    public TransactionException() {
    }

    public TransactionException(String message) {
        super(message);
    }

    public TransactionException(Throwable cause) {
        super(cause);
    }

    public TransactionException(String message, Throwable cause) {
        super(message, cause);
    }
}

package com.hyd.dao.exception;

public class LogException extends RuntimeException {

    public LogException(String message) {
        super(message);
    }

    public LogException(Throwable cause) {
        super(cause);
    }
}

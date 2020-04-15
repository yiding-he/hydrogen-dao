package com.hyd.dao.log;

public class LogException extends RuntimeException {

    public LogException(String message) {
        super(message);
    }

    public LogException(Throwable cause) {
        super(cause);
    }
}

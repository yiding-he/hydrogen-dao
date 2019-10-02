package com.hyd.dao;

/**
 * @author yiding.he
 */
public class DataConversionException extends DAOException {

    public DataConversionException() {
    }

    public DataConversionException(String message) {
        super(message);
    }

    public DataConversionException(Throwable cause) {
        super(cause);
    }

    public DataConversionException(String message, Throwable cause) {
        super(message, cause);
    }
}

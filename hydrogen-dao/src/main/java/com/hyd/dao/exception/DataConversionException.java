package com.hyd.dao.exception;

import com.hyd.dao.DAOException;

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

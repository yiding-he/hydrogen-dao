package com.hyd.dao.database.commandbuilder;

import com.hyd.dao.DAOException;

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

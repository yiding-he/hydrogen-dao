package com.hyd.dao.util;

import com.hyd.dao.DAOException;

/**
 * (description)
 * created at 2017/11/9
 *
 * @author yidin
 */
public class BeanException extends DAOException {

    public BeanException() {
    }

    public BeanException(String message) {
        super(message);
    }

    public BeanException(Throwable cause) {
        super(cause);
    }

    public BeanException(String message, Throwable cause) {
        super(message, cause);
    }
}

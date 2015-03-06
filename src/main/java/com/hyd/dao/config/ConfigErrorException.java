package com.hyd.dao.config;

import com.hyd.dao.DAOException;

/**
 * 配置读取异常类
 */
public class ConfigErrorException extends DAOException {

    public ConfigErrorException() {
    }

    public ConfigErrorException(String message) {
        super(message);
    }

    public ConfigErrorException(Throwable cause) {
        super(cause);
    }

    public ConfigErrorException(String message, Throwable cause) {
        super(message, cause);
    }
}

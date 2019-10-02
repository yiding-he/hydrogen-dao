package com.hyd.dao;

import com.hyd.dao.database.commandbuilder.Command;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

/**
 * DAO 的基础异常类
 */
@SuppressWarnings("unchecked")
public class DAOException extends RuntimeException {

    private final Command command; // 异常相关 SQL

    public Command getCommand() {
        return command;
    }

    public DAOException() {
        this.command = null;
    }

    public DAOException(String message) {
        super(message);
        this.command = null;
    }

    public DAOException(Throwable cause) {
        super(cause);
        this.command = null;
    }

    public DAOException(String message, Throwable cause) {
        super(message, cause);
        this.command = null;
    }

    public DAOException(String message, Command command) {
        super(message);
        this.command = command;
    }

    public DAOException(String message, Throwable cause, Command command) {
        super(message, cause);
        this.command = command;
    }

    public DAOException(String message, String sql, List<Object> params) {
        this(message, null, sql, params);
    }

    public DAOException(String message, Exception e, String sql, List<?> params) {
        super(message, e);
        List<Object> _params = params == null ? Collections.emptyList() : (List<Object>) params;
        this.command = new Command(sql, _params);
    }

    public int getSqlErrorNumber() {
        if (getCause() instanceof SQLException) {
            return ((SQLException) getCause()).getErrorCode();
        }
        return -1;
    }

    @Override
    public String toString() {
        if (this.command == null) {
            return super.toString();
        } else {
            String cause = "";
            if (getCause() instanceof SQLException) {
                cause = getCause().toString().trim();
            }

            return super.toString().trim() +
                    "\n  --SQL   : " + this.command.getStatement() +
                    "\n  --Params: " + this.command.getParams() +
                    "\n  --Cause : " + cause + "\n";

            // 上面两个地方用了 trim() 是因为 Oracle 的异常信息字符串最后会有一个换行
        }

    }
}

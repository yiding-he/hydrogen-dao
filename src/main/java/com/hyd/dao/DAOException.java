package com.hyd.dao;

import com.hyd.dao.database.commandbuilder.Command;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO 的基础异常类
 */
public class DAOException extends RuntimeException {

    private Command command; // 异常相关 SQL

    public Command getCommand() {
        return command;
    }

    public void setCommand(Command command) {
        this.command = command;
    }

    public DAOException() {
    }

    public DAOException(String message) {
        super(message);
    }

    public DAOException(Throwable cause) {
        super(cause);
    }

    public DAOException(String message, Throwable cause) {
        super(message, cause);
    }

    public DAOException(String message, String sql, List<? extends Object> params) {
        this(message, null, sql, params);
    }

    public DAOException(String message, Exception e, String sql, List<? extends Object> params) {
        this(message, e);
        if (params == null) {
            params = new ArrayList<Object>();
        }
        this.command = new Command(sql, params);
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

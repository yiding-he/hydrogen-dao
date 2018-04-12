package com.hyd.dao.src.classdef;

import com.hyd.dao.src.ClassDef;
import com.hyd.dao.src.fx.ConnectionManager;

import java.sql.Types;

/**
 * (description)
 * created at 2018/4/12
 *
 * @author yidin
 */
public abstract class ClassDefBuilder {

    protected ConnectionManager connectionManager;

    public ClassDefBuilder(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    public abstract ClassDef build(String tableName);

    protected String getJavaType(int dataType) {
        switch (dataType) {
            case Types.VARCHAR:
            case Types.CHAR:
            case Types.LONGVARCHAR:
                return "String";
            case Types.BIT:
                return "boolean";
            case Types.NUMERIC:
                return "java.math.BigDecimal";
            case Types.TINYINT:
                return "byte";
            case Types.SMALLINT:
                return "short";
            case Types.INTEGER:
                return "int";
            case Types.BIGINT:
                return "long";
            case Types.REAL:
            case Types.FLOAT:
                return "float";
            case Types.DOUBLE:
                return "double";
            case Types.VARBINARY:
            case Types.BINARY:
                return "byte[]";
            case Types.DATE:
                return "java.sql.Date";
            case Types.TIME:
                return "java.sql.Time";
            case Types.TIMESTAMP:
                return "java.sql.Timestamp";
            default:
                return "String";
        }
    }
}

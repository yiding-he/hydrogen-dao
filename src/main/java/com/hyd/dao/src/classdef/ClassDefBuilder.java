package com.hyd.dao.src.classdef;

import com.hyd.dao.database.ColumnInfo;
import com.hyd.dao.database.DatabaseType;
import com.hyd.dao.src.ClassDef;

import java.sql.Types;

/**
 * (description)
 * created at 2018/4/12
 *
 * @author yidin
 */
public abstract class ClassDefBuilder {

    protected String tableName;

    protected ColumnInfo[] columnInfos;

    protected DatabaseType databaseType;

    public ClassDefBuilder(
            String tableName,
            ColumnInfo[] columnInfos,
            DatabaseType databaseType) {

        this.tableName = tableName;
        this.columnInfos = columnInfos;
        this.databaseType = databaseType;
    }

    public abstract ClassDef build(String tableName);

    protected String getJavaType(int dataType) {
        switch (dataType) {
            case Types.VARCHAR:
            case Types.CHAR:
            case Types.LONGVARCHAR:
                return "String";
            case Types.BIT:
                return "Boolean";
            case Types.NUMERIC:
                return "java.math.BigDecimal";
            case Types.TINYINT:
                return "Byte";
            case Types.SMALLINT:
                return "Short";
            case Types.INTEGER:
                return "Integer";
            case Types.BIGINT:
                return "Long";
            case Types.REAL:
            case Types.FLOAT:
                return "Float";
            case Types.DOUBLE:
                return "Double";
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
                return getJavaTypeByDatabase(dataType);
        }
    }

    private String getJavaTypeByDatabase(int dataType) {
        switch (databaseType) {
            case MySQL:
                return getMySQLJavaType(dataType);
            case Oracle:
                return getOracleJavaType(dataType);
            default:
                return "String";
        }
    }

    private String getMySQLJavaType(int dataType) {
        switch (dataType) {
            case 3:
                return "Double";
            default:
                return "String";
        }
    }

    private String getOracleJavaType(int dataType) {
        return "String";
    }
}

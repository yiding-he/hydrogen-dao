package com.hyd.dao.util;

import com.hyd.dao.database.type.BlobReader;
import com.hyd.dao.database.type.ClobUtil;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 处理数据库中的值类型的类
 */
public class TypeUtil {

    public static final String[] DATE_PATTERNS = {
            "yyyy-MM-dd HH:mm:ss",
            "yyyy-MM-dd HH:mm:ss.SSS",
            "yyyy-MM-dd",
    };

    /**
     * 判断当前进程中是否加载了指定的类
     *
     * @param className 类名
     *
     * @return 如果加载了则返回 true
     */
    public static boolean classExists(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    /**
     * 根据字段数据类型将数据库中的值转化为 Java 类型，用于对简单查询结果的转换
     * 转换结果将放入 Row 对象，以帮助进行进一步转换。
     * <p/>
     * 数字类型 -> BigDecimal
     * CLOB -> String
     * BLOB -> byte[]
     * 其他类型保持原样
     *
     * @param columnType 值的 SQL 类型
     * @param value      值
     *
     * @return 转化后的类型
     *
     * @throws java.io.IOException   如果读取 LOB 流失败
     * @throws java.sql.SQLException 如果读取 LOB 字段失败
     */
    public static Object convertDatabaseValue(int columnType, Object value) throws IOException, SQLException {
        if (value == null) {
            return null;
        } else if (isNumericType(columnType)) {
            return (value instanceof BigDecimal ? value : new BigDecimal(value.toString()));
        } else if (isDateType(columnType)) {
            return toDate(value);
        } else if (value instanceof Clob) {
            return ClobUtil.read((Clob) value);
        } else if (value instanceof Blob) {
            return BlobReader.readBytes((Blob) value);
        }
        return value;
    }

    public static boolean isNumericType(int columnType) {
        return columnType == Types.NUMERIC || columnType == Types.INTEGER
                || columnType == Types.BIGINT || columnType == Types.REAL
                || columnType == Types.DECIMAL || columnType == Types.FLOAT
                || columnType == Types.DOUBLE;
    }

    public static boolean isDateType(int columnType) {
        return columnType == Types.DATE || columnType == Types.TIME || columnType == Types.TIMESTAMP;
    }

    private static Date toDate(Object value) {

        if (value instanceof Date) {
            return (Date) value;
        }

        Class<?> type = value.getClass();

        if (type == String.class) {
            return toDateFromString(value.toString());
        }

        try {
            if (type.getDeclaredMethod("dateValue") != null) {
                return (Date) type.getDeclaredMethod("dateValue").invoke(value);
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }

        throw new IllegalStateException("Value of type " + type + " cannot be cast to Date");
    }

    private static Date toDateFromString(String s) {
        for (String pattern : DATE_PATTERNS) {
            try {
                return new SimpleDateFormat(pattern).parse(s);
            } catch (ParseException e) {
                // ignore
            }
        }

        throw new IllegalStateException("Unable to parse date string '" + s + "'");
    }

    /**
     * 对用户提供的执行参数进行一些修复
     *
     * @param obj 参数值
     *
     * @return 修复后的参数
     */
    public static Object cconvertParamValue(Object obj) {
        if (obj == null) {
            return "";
        } else if (obj.getClass().equals(Date.class)) {
            // 将 Date 转化为 TimeStamp，以避免时间丢失
            return new Timestamp(((Date) obj).getTime());
        } else {
            return obj;
        }

    }
}

package com.hyd.dao.mate.util;

import com.hyd.dao.DAOException;
import com.hyd.dao.database.type.BlobReader;
import com.hyd.dao.database.type.ClobUtil;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Timestamp;
import java.sql.Types;
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
     * 根据字段数据类型将数据库中的值转化为 Java 类型，用于对简单查询结果的转换
     * 转换结果将放入 Row 对象，以帮助进行进一步转换。
     * <p>
     * 数字类型 -&gt; BigDecimal
     * CLOB -&gt; String
     * BLOB -&gt; byte[]
     * 其他类型保持原样
     *
     * @param columnType 值的 SQL 类型
     * @param value      值
     *
     * @return 转化后的类型
     *
     * @throws DAOException 如果读取 LOB 失败
     */
    public static Object convertDatabaseValue(int columnType, Object value) {
        try {
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
        } catch (Exception e) {
            throw DAOException.wrap(e);
        }
    }

    private static boolean isNumericType(int columnType) {
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
            return (Date) type.getDeclaredMethod("dateValue").invoke(value);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("Value of type " + type + " cannot be cast to Date");
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
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
     * 对 SQL 的参数进行转换
     *
     * @param obj 参数值
     *
     * @return 修复后的参数
     */
    public static Object convertParamValue(Object obj) {
        if (obj == null) {
            return "";
        } else if (obj.getClass().isEnum()) {
            return ((Enum<?>) obj).name();
        } else if (obj.getClass().equals(Date.class)) {
            return new Timestamp(((Date) obj).getTime());     // 将 Date 转化为 TimeStamp，以避免时间丢失
        } else {
            return obj;
        }
    }

    public static Field getFieldIgnoreCase(Class<?> type, String fieldName) {
        if (type == Object.class) {
            return null;
        }

        Field[] fields = type.getDeclaredFields();
        for (Field field : fields) {
            if (field.getName().equalsIgnoreCase(fieldName)) {
                return field;
            }
        }

        return getFieldIgnoreCase(type.getSuperclass(), fieldName);
    }
}

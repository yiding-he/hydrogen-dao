package com.hyd.dao;

import com.hyd.dao.log.Logger;
import com.hyd.dao.util.CaseInsensitiveHashMap;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * 表示查询结果中的一行。在所有的方法中，字段名不分大小写。
 *
 * @author yiding.he
 */
public class Row extends CaseInsensitiveHashMap<Object> implements Map<String, Object> {

    /**
     * 缺省日期格式
     */
    public static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";

    private static final Logger LOG = Logger.getLogger(Row.class);

    public Double getDoubleObject(String key) {
        Object value = get(key);
        if (value == null) {
            return null;
        } else if (value instanceof String) {
            return Double.parseDouble((String) value);
        } else if (value instanceof Double) {
            return (Double) value;
        } else if (value instanceof Date) {
            return (double) ((Date) value).getTime();
        } else {
            return Double.parseDouble(value.toString());
        }
    }

    public double getDouble(String key, double defaultValue) {
        try {
            Double value = getDoubleObject(key);
            return value == null ? defaultValue : value;
        } catch (Exception e) {
            LOG.warn(e.getMessage(), e);
            return defaultValue;
        }
    }

    /**
     * 以 Long 类型获取字段的值
     *
     * @param key 字段名
     *
     * @return 字段值
     */
    public Long getLongObject(String key) {
        Object value = get(key);
        if (value == null) {
            return null;
        } else if (value instanceof String) {
            return Long.parseLong((String) value);
        } else if (value instanceof Double) {
            return ((Double) value).longValue();
        } else if (value instanceof Date) {
            return ((Date) value).getTime();
        } else {
            return Long.parseLong(value.toString());
        }
    }

    /**
     * 以 long 类型获取字段的值
     *
     * @param key          字段名
     * @param defaultValue 缺省值
     *
     * @return 字段值。如果值不存在，则返回 defaultValue
     */
    public long getLong(String key, long defaultValue) {
        try {
            Long l = getLongObject(key);
            return l == null ? defaultValue : l;
        } catch (Exception e) {
            LOG.warn(e.getMessage(), e);
            return defaultValue;
        }
    }

    /**
     * 以 Integer 类型获取字段的值
     *
     * @param key 字段名
     *
     * @return 字段的值
     */
    public Integer getIntegerObject(String key) {
        Object value = get(key);
        if (value == null) {
            return null;
        } else if (value instanceof String) {
            return Integer.parseInt((String) value);
        } else if (value instanceof Double) {
            return ((Double) value).intValue();
        } else if (value instanceof Date) {
            return (int) ((Date) value).getTime();
        } else {
            return Integer.parseInt(value.toString());
        }
    }

    /**
     * 以 int 类型获取字段的值
     *
     * @param key          字段名
     * @param defaultValue 缺省值
     *
     * @return 字段值。如果值不存在，则返回 defaultValue
     */
    public int getInteger(String key, int defaultValue) {
        try {
            Integer i = getIntegerObject(key);
            return i == null ? defaultValue : i;
        } catch (Exception e) {
            LOG.warn(e.getMessage(), e);
            return defaultValue;
        }
    }

    /**
     * 以 Date 类型获取字段的值。
     * <ul>
     * <li>如果字段是日期类型，则直接返回其值；</li>
     * <li>如果字段是字符串类型，则按照 {@link #DEFAULT_DATE_PATTERN} 来解析；</li>
     * <li>如果字段是数字类型，则将其看作是自 1970 年 1 月 1 日以来的毫秒数来解析。</li>
     * </ul>
     *
     * @param key 字段名
     *
     * @return 字段的值
     */
    public Date getDate(String key) {
        return getDate(key, DEFAULT_DATE_PATTERN);
    }

    /**
     * 以 Date 类型获取字段的值
     * <ul>
     * <li>如果字段是日期类型，则直接返回其值；</li>
     * <li>如果字段是字符串类型，则按照 pattern 参数来解析；</li>
     * <li>如果字段是数字类型，则将其看作是自 1970 年 1 月 1 日以来的毫秒数来解析。</li>
     * </ul>
     *
     * @param key     字段名
     * @param pattern 如果字段是字符串类型，则表示日期格式
     *
     * @return 字段的值
     */
    public Date getDate(String key, String pattern) {
        Object value = get(key);
        if (value == null) {
            return null;
        } else if (value instanceof String) {
            try {
                return parseDate((String) value, pattern);
            } catch (ParseException e) {
                throw new DAOException(e);
            }
        } else if (value instanceof Double) {
            return new Date(((Double) value).longValue());
        } else if (value instanceof Date) {
            return (Date) value;
        } else {
            try {
                return parseDate(value.toString(), pattern);
            } catch (ParseException e) {
                throw new DAOException(e);
            }
        }
    }

    private Date parseDate(String dateStr, String pattern) throws ParseException {
        return new SimpleDateFormat(pattern).parse(dateStr);
    }

    public String getString(String key) {
        Object value = get(key);
        if (value == null) {
            return null;
        } else {
            return value.toString();
        }
    }
}

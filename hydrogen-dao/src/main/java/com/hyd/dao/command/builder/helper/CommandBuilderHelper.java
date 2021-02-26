package com.hyd.dao.command.builder.helper;

import com.hyd.dao.DAO;
import com.hyd.dao.database.ColumnInfo;
import com.hyd.dao.database.ConnectionContext;
import com.hyd.dao.database.FQN;
import com.hyd.dao.database.type.NameConverter;
import com.hyd.dao.exception.DataConversionException;
import com.hyd.dao.mate.util.BeanUtil;
import com.hyd.dao.mate.util.Cls;
import com.hyd.dao.mate.util.Str;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 用于构造 SQL 命令的帮助类，隐藏不同数据库之间的区别
 */
public class CommandBuilderHelper {

    /**
     * 获得指定库表的字段信息
     *
     * @param fqn 表名信息
     *
     * @return 表的字段信息
     */
    public static List<ColumnInfo> getColumnInfos(FQN fqn, ConnectionContext context) {
        return ColumnInfoHelper.getColumnInfo(fqn, context.getConnection());
    }

    /**
     * 生成 SQL 语句参数
     *
     * @param infos  字段信息
     * @param object 提供字段的参数值的对象
     *
     * @return 生成的 SQL 语句参数
     */
    public static List<Object> generateParams(List<ColumnInfo> infos, Object object, NameConverter nameConverter) {
        List<Object> params = new ArrayList<>();
        for (ColumnInfo info : infos) {
            if (info.getDataType() != DAO.SYSDATE_TYPE) {
                params.add(generateParamValue(object, info, nameConverter));
            }
        }
        return params;
    }

    /**
     * 根据 bean 类型过滤字段列表，删除类型中没有定义的字段
     */
    public static List<ColumnInfo> filterColumnsByType(List<ColumnInfo> original, Class<?> type, NameConverter nameConverter) {
        // 如果类型不是 POJO 而是 Map 则无需过滤，原样返回
        if (Map.class.isAssignableFrom(type)) {
            return new ArrayList<>(original);
        } else {
            List<ColumnInfo> infoList = new ArrayList<>();
            for (ColumnInfo info : original) {
                String field = nameConverter.column2Field(info.getColumnName());
                if (Cls.hasField(type, field)) {
                    infoList.add(info);
                }
            }
            return infoList;
        }
    }

    /**
     * 根据字段信息，从对象中取得相应的属性值
     *
     * @param object 对象
     * @param info   字段信息
     *
     * @return 属性值。如果获取失败或需要跳过该字段则返回 null
     */
    public static Object generateParamValue(Object object, ColumnInfo info, NameConverter nameConverter) {
        String fieldName = nameConverter.column2Field(info.getColumnName());

        String strValue;
        Object value;

        // 如果 object 是一个 Map，则根据字段名取值；否则根据属性名取值。
        if (object instanceof Map) {
            Map map = (Map) object;
            value = map.get(info.getColumnName());
            if (value == null) {
                value = map.get(info.getColumnName().toUpperCase(Locale.ENGLISH));
            }
            if (value == null) {
                value = map.get(info.getColumnName().toLowerCase());
            }
        } else {
            Field field = getObjectField(object, fieldName);
            if (field == null) {
                return null;
            }

            value = BeanUtil.getValue(object, fieldName);
        }

        if (value == null) {
            return null;
        }

        strValue = Str.valueOf(value);

        // 获取返回值
        switch (info.getDataType()) {
            case Types.NUMERIC:     // 1. 如果是数字类型的字段，则根据 strValue 进行转换；
            case Types.DECIMAL:
            case Types.BIGINT:
            case Types.DOUBLE:
            case Types.FLOAT:
            case Types.INTEGER:
                if (Str.isEmptyString(strValue)) {
                    return null;
                } else {
                    try {
                        return new BigDecimal(strValue);
                    } catch (NumberFormatException e) {
                        throw new DataConversionException(
                            "Conversion from value '" + strValue + "' to column " + info + " failed.", e);
                    }
                }

            case Types.DATE:        // 2. 如果是日期类型的字段，则直接从 Map 或 Bean 中获取；
            case Types.TIME:
            case Types.TIMESTAMP:
                if (object instanceof Map) {
                    return value;
                } else {
                    return BeanUtil.getValue(object, fieldName);
                }

            case Types.BLOB:        // 3. LOB 类型可以传入原值
            case Types.CLOB:
            case Types.NCLOB:
                return value;
            default:                // 4. 其他类型则直接使用 string_value。
                return strValue;
        }
    }

    private static Field getObjectField(Object object, String fieldName) {
        Field field = null;
        Class type = object.getClass();

        while (field == null && type != null) {
            try {
                field = type.getDeclaredField(fieldName);
            } catch (Exception e) {
                type = type.getSuperclass();
            }
        }

        return field;
    }

}

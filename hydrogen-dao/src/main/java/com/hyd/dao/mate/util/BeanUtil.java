package com.hyd.dao.mate.util;

import com.hyd.dao.DAOException;
import com.hyd.dao.Table;
import com.hyd.dao.database.type.TypeConverter;
import com.hyd.dao.time.UniTime;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.temporal.Temporal;
import java.util.*;

/**
 * 处理 bean 对象的帮助类
 */
@SuppressWarnings("unchecked")
public class BeanUtil {

    private BeanUtil() {

    }

    /**
     * 设置一个对象的属性
     *
     * @param obj       要设置的对象
     * @param fieldName 属性名（忽略大小写）
     * @param value     值
     */
    public static void setValueIgnoreCase(final Object obj, final String fieldName, final Object value) {
        if (value == null) {
            return;
        }

        try {
            var propertyDescriptor = getPropertyDescriptor(obj.getClass(), fieldName);
            if (propertyDescriptor == null) {
                throw new DAOException(
                    "Field not found for " + obj.getClass().getCanonicalName() + "#" + fieldName);
            }

            var fieldType = propertyDescriptor.getPropertyType();
            var convertedValue = convertValue(value, fieldType);

            var writeMethod = getPropertyMethod(obj.getClass(), fieldName, false);
            if (writeMethod == null) {
                throw new DAOException(
                    "Missing write method for " + obj.getClass().getCanonicalName() + "#" + fieldName);
            }

            writeMethod.invoke(obj, convertedValue);

        } catch (DAOException e) {
            throw e;
        } catch (Exception e) {
            throw new DAOException("Cannot set property " + obj.getClass().getCanonicalName() + "#" + fieldName, e);
        }
    }

    /**
     * 获得一个属性的 getter 或 setter 方法
     *
     * @param clazz     包含属性的类
     * @param fieldName 属性名
     * @param getter    是否取 getter 方法。如果是 false，则表示取 setter 方法。
     *
     * @return 方法对象
     */
    private static Method getPropertyMethod(Class clazz, String fieldName, boolean getter) {
        var descriptor = getPropertyDescriptor(clazz, fieldName);
        return descriptor == null ? null :
            getter ? descriptor.getReadMethod() : descriptor.getWriteMethod();
    }

    private static PropertyDescriptor getPropertyDescriptor(Class clazz, String fieldName) {

        try {
            var beanInfo = Introspector.getBeanInfo(clazz);
            for (var descriptor : beanInfo.getPropertyDescriptors()) {
                if (descriptor.getName().equalsIgnoreCase(fieldName)) {
                    return descriptor;
                }
            }

            return null;
        } catch (IntrospectionException e) {
            throw new BeanException("Error parsing class '" + clazz.getCanonicalName() + "'", e);
        }
    }

    /**
     * 将一个值转化为指定的属性类型，以便于赋到对象属性。注意，当 value 的值超过属性类型允许的最大值时，强制转换将起作用。
     * <p>
     * 本方法主要处理数字类型的查询结果，对于字符类型和日期类型则不作处理，直接赋值。
     *
     * @param value 值
     * @param clazz 属性类型
     *
     * @return 转化后的属性值
     *
     * @throws NoSuchMethodException  如果 field 指名的类不包含一个以字符串为参数的构造函数
     * @throws IllegalAccessException 如果执行构造函数失败
     * @throws InstantiationException 如果执行构造函数失败
     */
    private static Object convertValue(Object value, Class clazz)
        throws NoSuchMethodException, IllegalAccessException, InstantiationException {
        if (value == null) {
            return null;
        }

        // 如果类型刚好相符就直接返回 value
        if (value.getClass() == clazz ||
            (clazz.isPrimitive() && TypeConverter.getWrapper(clazz) == value.getClass())) {
            return value;
        }

        if (clazz == String.class) {
            return String.valueOf(value);

        } else if (clazz == UniTime.class && value instanceof Date dateValue) {
            return UniTime.fromDate(dateValue);

        } else if (clazz == UniTime.class && value instanceof Temporal temporalValue) {
            return UniTime.fromTemporal(temporalValue);

        } else if (clazz == Integer.class || clazz == Long.class || clazz == Double.class ||
            clazz == BigDecimal.class || clazz == BigInteger.class) {
            try {
                var str_value = new BigDecimal(String.valueOf(value)).toPlainString();

                // 避免因为带有小数点而无法转换成 Integer/Long。
                // 但如果值真的带小数，那么为了避免精度丢失，只有抛出异常了。
                if (str_value.endsWith(".0")) {
                    str_value = str_value.substring(0, str_value.length() - 2);
                }

                return clazz.getDeclaredConstructor(String.class).newInstance(str_value);

            } catch (InvocationTargetException e) {
                if (e.getTargetException() instanceof NumberFormatException) {
                    var message = "Value " + value + " (" + value.getClass() + ") cannot convert to " + clazz;
                    throw new DAOException(message, e);
                }
            }
        } else if (clazz == Boolean.TYPE || clazz == Boolean.class) {
            return Boolean.valueOf(String.valueOf(value));

        } else if (clazz.isPrimitive()) { // 处理基本型别

            var bdValue = new BigDecimal(String.valueOf(value));

            if (clazz == Integer.TYPE) {

                if (bdValue.compareTo(new BigDecimal(Integer.MAX_VALUE)) > 0) {
                    throw new DAOException("Value " + bdValue + " is too large for integer");
                }
                return bdValue.intValue();

            } else if (clazz == Long.TYPE) {

                if (bdValue.compareTo(new BigDecimal(Long.MAX_VALUE)) > 0) {
                    throw new DAOException("Value " + bdValue + " is too large for long");
                }
                return bdValue.longValue();

            } else if (clazz == Double.TYPE) {

                if (bdValue.compareTo(BigDecimal.valueOf(Double.MAX_VALUE)) > 0) {
                    throw new DAOException("Value " + bdValue + " is too large for double");
                }
                return bdValue.doubleValue();

            } else if (clazz == Byte.TYPE) {

                if (bdValue.compareTo(new BigDecimal(Byte.MAX_VALUE)) > 0) {
                    throw new DAOException("Value " + bdValue + " is too large for byte");
                }
                return bdValue.byteValue();

            } else if (clazz == Short.TYPE) {

                if (bdValue.compareTo(new BigDecimal(Short.MAX_VALUE)) > 0) {
                    throw new DAOException("Value " + bdValue + " is too large for short");
                }
                return bdValue.shortValue();

            } else if (clazz == Float.TYPE) {

                if (bdValue.compareTo(BigDecimal.valueOf(Float.MAX_VALUE)) > 0) {
                    throw new DAOException("Value " + bdValue + " is too large for float");
                }
                return bdValue.floatValue();
            }
        }
        return value;
    }


    /**
     * 获得一个对象的属性
     *
     * @param obj       要获取的对象
     * @param fieldName 属性名
     *
     * @return 值。如果对象中没有该属性或该属性不可读，则返回null
     */
    public static Object getValue(Object obj, String fieldName) {
        Method getter = null;
        try {
            getter = getPropertyMethod(obj.getClass(), fieldName, true);
            return getter == null ? null : getter.invoke(obj);
        } catch (Exception e) {
            if (getter == null) {
                throw new DAOException(
                    "Error getting property " + obj.getClass().getCanonicalName() + "#" + fieldName, e);
            } else {
                throw new DAOException(
                    "Error executing method " + obj.getClass().getCanonicalName() + "#" +
                        getter.getName() + " " + modifiers(getter.getModifiers()), e);
            }
        }
    }

    public static List<String> modifiers(int modifiers) {
        List<String> result = new ArrayList<>();
        if (Modifier.isStatic(modifiers)) {
            result.add("static");
        }
        if (Modifier.isAbstract(modifiers)) {
            result.add("abstract");
        }
        if (Modifier.isFinal(modifiers)) {
            result.add("final");
        }
        if (Modifier.isPrivate(modifiers)) {
            result.add("private");
        }
        if (Modifier.isProtected(modifiers)) {
            result.add("protected");
        }
        if (Modifier.isPublic(modifiers)) {
            result.add("public");
        }
        if (Modifier.isSynchronized(modifiers)) {
            result.add("synchronized");
        }
        return result;
    }

    /**
     * 获得指定类的 static 属性的值。该方法首先尝试直接获取属性的值，如果失败，则尝试通过静态 getter 方法；如果还不行则返回 null。
     *
     * @param clazz     指定的类
     * @param fieldName 属性名
     *
     * @return 属性的值。
     *
     * @throws Exception 如果属性不存在
     */
    private static Object getStaticValue(Class<?> clazz, String fieldName) throws Exception {
        try {
            var field = TypeUtil.getFieldIgnoreCase(clazz, fieldName);
            if (field == null) {
                return null;
            }

            if (Modifier.isStatic(field.getModifiers())) {
                return null;
            }
            return field.get(null);
        } catch (IllegalAccessException e) {
            return clazz.getMethod(getGetMethodName(fieldName)).invoke(null);
        }
    }

    private static String getGetMethodName(String fieldName) {
        return "get" + capitalize(fieldName);
    }

    private static String capitalize(String str) {
        return str.substring(0, 1).toUpperCase(Locale.ENGLISH) + str.substring(1);
    }

    /**
     * 按照指定的 sortingKey 对 Map 数组进行排序
     *
     * @param maps       要排序的 Map 数组
     * @param sortingKey 指定用于排序的 sortingKey
     */
    public static <K, V extends Comparable<V>> void sort(Map<K, V>[] maps, K sortingKey, V defaultValue) {
        sort(Arrays.asList(maps), sortingKey, defaultValue);
    }
    public static <K, V extends Comparable<V>> void sort(List<Map<K, V>> maps, K sortingKey, V defaultValue) {
        maps.sort(Comparator.comparing(map -> map.getOrDefault(sortingKey, defaultValue)));
    }

    /**
     * 获取指定 pojo 类对应的表名
     *
     * @param type pojo 类
     *
     * @return 表名。如果没有得到表名则会抛出异常。
     */
    public static String getTableName(Class<?> type) {
        var t = type.getAnnotation(Table.class);

        try {
            return t == null ?
                (String) getStaticValue(type, "TN") : t.name();
        } catch (Exception e) {
            throw new DAOException("No table related to " + type, e);
        }
    }
}

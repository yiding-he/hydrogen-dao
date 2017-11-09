package com.hyd.dao.util;

import com.hyd.dao.DAOException;
import com.hyd.dao.Table;
import com.hyd.dao.database.type.TypeConverter;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 处理 bean 对象的帮助类
 */
@SuppressWarnings("unchecked")
public class BeanUtil {

    private static Map<Class, Map<String, PropertyDescriptor>>
            propertyDescriptorMap = MapCacheUtils.newLRUCache(100, true);

    /**
     * 设置一个对象的属性
     *
     * @param obj       要设置的对象
     * @param fieldName 属性名
     * @param value     值
     */
    public static void setValue(Object obj, String fieldName, Object value) {
        if (value == null) {
            return;
        }

        try {
            Class<?> fieldType = getPropertyDescriptor(obj.getClass(), fieldName).getPropertyType();
            value = convertValue(value, fieldType);
            Method writeMethod = getPropertyMethod(obj.getClass(), fieldName, false);
            if (writeMethod != null) {
                writeMethod.invoke(obj, value);
            } else {
                throw new DAOException(
                        "Missing write method for " + obj.getClass().getCanonicalName() + "#" + fieldName);
            }
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
     *
     * @throws IntrospectionException 如果分析 JavaBean 失败
     */
    private static Method getPropertyMethod(Class clazz, String fieldName, boolean getter)
            throws IntrospectionException {
        PropertyDescriptor descriptor = getPropertyDescriptor(clazz, fieldName);
        return descriptor == null ? null :
                getter ? descriptor.getReadMethod() : descriptor.getWriteMethod();
    }

    private static PropertyDescriptor getPropertyDescriptor(
            Class clazz, String fieldname) throws IntrospectionException {

        Map<String, PropertyDescriptor> pdMap = BeanUtil.propertyDescriptorMap.computeIfAbsent(clazz, __ -> {
            try {
                Map<String, PropertyDescriptor> map = new HashMap<>();
                BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
                for (PropertyDescriptor descriptor : beanInfo.getPropertyDescriptors()) {
                    map.put(descriptor.getName(), descriptor);
                }
                return map;
            } catch (IntrospectionException e) {
                throw new BeanException("Error parsing class '" + clazz.getCanonicalName() + "'", e);
            }
        });

        return pdMap.get(fieldname);
    }

    /**
     * 将一个值转化为指定的属性类型，以便于赋到对象属性。注意，当 value 的值超过属性类型允许的最大值时，强制转换将起作用。
     * <p/>
     * 本方法主要处理数字类型的查询结果，对于字符类型和日期类型则不作处理，直接赋值。
     *
     * @param value 值
     * @param clazz 属性类型
     *
     * @return 转化后的属性值
     *
     * @throws NoSuchMethodException     如果 field 指名的类不包含一个以字符串为参数的构造函数
     * @throws InvocationTargetException 如果执行构造函数失败
     * @throws IllegalAccessException    如果执行构造函数失败
     * @throws InstantiationException    如果执行构造函数失败
     */
    protected static Object convertValue(Object value, Class clazz)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
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

        } else if (clazz == Integer.class || clazz == Long.class || clazz == Double.class ||
                clazz == BigDecimal.class || clazz == BigInteger.class) {
            try {
                String str_value = new BigDecimal(String.valueOf(value)).toPlainString();

                // 避免因为带有小数点而无法转换成 Integer/Long。
                // 但如果值真的带小数，那么为了避免精度丢失，只有抛出异常了。
                if (str_value.endsWith(".0")) {
                    str_value = str_value.substring(0, str_value.length() - 2);
                }

                return clazz.getDeclaredConstructor(String.class).newInstance(str_value);

            } catch (InvocationTargetException e) {
                if (e.getTargetException() instanceof NumberFormatException) {
                    throw new DAOException("Value " + value + " (" + value.getClass() + ") cannot convert to " + clazz);
                }
            }
        } else if (clazz == Boolean.TYPE) {
            return Boolean.valueOf(String.valueOf(value));

        } else if (clazz.isPrimitive() && clazz != Boolean.TYPE) { // 处理基本型别

            BigDecimal bdValue = new BigDecimal(String.valueOf(value));

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

                if (bdValue.compareTo(new BigDecimal(Double.MAX_VALUE)) > 0) {
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

                if (bdValue.compareTo(new BigDecimal(Float.MAX_VALUE)) > 0) {
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
        try {
            Method getter = getPropertyMethod(obj.getClass(), fieldName, true);
            if (getter != null) {
                return getter.invoke(obj);
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new DAOException("Error getting property " + obj.getClass().getCanonicalName() + "#" + fieldName, e);
        }
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
    public static Object getStaticValue(Class clazz, String fieldName) throws Exception {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            if ((field.getModifiers() & Modifier.STATIC) == 0) {
                return null;
            }
            return field.get(null);
        } catch (IllegalAccessException e) {
            return clazz.getMethod(getGetMethodName(fieldName)).invoke(null);
        }
    }

    private static String getGetMethodName(String fieldname) {
        return "get" + capitalize(fieldname);
    }

    private static String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    /**
     * 按照指定的 key 对 Map 数组进行排序
     *
     * @param maps       要排序的 Map 数组
     * @param columnName 指定用于排序的 key
     */
    public static void sort(Map[] maps, String columnName) {
        ComparableMap[] cmaps = new ComparableMap[maps.length];
        for (int i = 0; i < cmaps.length; i++) {
            cmaps[i] = new ComparableMap(maps[i], columnName);
        }

        Arrays.sort(cmaps);
        for (int i = 0; i < maps.length; i++) {
            maps[i] = cmaps[i].map;
        }
    }

    /**
     * 获取指定 pojo 类对应的表名
     *
     * @param type pojo 类
     *
     * @return 表名。如果没有得到表名则会抛出异常。
     */
    public static String getTableName(Class<?> type) {
        Table t = type.getAnnotation(Table.class);

        try {
            return t == null ?
                    (String) getStaticValue(type, "TN") : t.name();
        } catch (Exception e) {
            throw new DAOException("No table related to " + type, e);
        }
    }

    private static class ComparableMap implements Comparable<ComparableMap> {

        public Map map;

        public String columnName;

        public ComparableMap(Map map, String columnName) {
            this.map = map;
            this.columnName = columnName;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof ComparableMap)) {
                return false;
            }

            ComparableMap m = (ComparableMap) obj;
            Object value = this.map.get(this.columnName);
            return value != null && value.equals(m.map.get(this.columnName));
        }

        @SuppressWarnings("NullableProblems")
        public int compareTo(ComparableMap o) {

            if (o == null) {
                return 1;
            }

            Object thisValue = map.get(columnName);
            Object thatValue = o.map.get(o.columnName);

            if (thisValue == null) {
                if (thatValue == null) {
                    return 0;
                }
                return -1;
            } else if (thatValue == null) {
                return 1;
            }

            if (thisValue instanceof Comparable && thatValue instanceof Comparable) {
                return ((Comparable) thisValue).compareTo(thatValue);
            } else {
                return thisValue.toString().compareTo(thatValue.toString());
            }
        }
    }
}

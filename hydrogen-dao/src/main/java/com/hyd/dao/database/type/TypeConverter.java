package com.hyd.dao.database.type;

import com.hyd.dao.log.Logger;
import com.hyd.dao.mate.util.BeanUtil;
import com.hyd.dao.mate.util.TypeUtil;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

/**
 * 将查询结果封装为 pojo 对象的类
 */
@SuppressWarnings({"unchecked"})
public class TypeConverter {

    private static final Logger LOG = Logger.getLogger(TypeConverter.class);

    private static final Set<String> warnedTypes = new HashSet<>();

    static Map<Class, Class> primitiveToWrapper = new HashMap<>();

    static Map<Class, Class> wrapperToPrimitive = new HashMap<>();

    static {
        primitiveToWrapper.put(Boolean.TYPE, Boolean.class);
        primitiveToWrapper.put(Byte.TYPE, Byte.class);
        primitiveToWrapper.put(Short.TYPE, Short.class);
        primitiveToWrapper.put(Character.TYPE, Character.class);
        primitiveToWrapper.put(Integer.TYPE, Integer.class);
        primitiveToWrapper.put(Long.TYPE, Long.class);
        primitiveToWrapper.put(Float.TYPE, Float.class);
        primitiveToWrapper.put(Double.TYPE, Double.class);
        wrapperToPrimitive.put(Boolean.class, Boolean.TYPE);
        wrapperToPrimitive.put(Byte.class, Byte.TYPE);
        wrapperToPrimitive.put(Short.class, Short.TYPE);
        wrapperToPrimitive.put(Character.class, Character.TYPE);
        wrapperToPrimitive.put(Integer.class, Integer.TYPE);
        wrapperToPrimitive.put(Long.class, Long.TYPE);
        wrapperToPrimitive.put(Float.class, Float.TYPE);
        wrapperToPrimitive.put(Double.class, Double.TYPE);
    }

    private TypeConverter() {

    }

    public static Class getPrimitive(Class wrapperClass) {
        return wrapperToPrimitive.get(wrapperClass);
    }

    public static Class getWrapper(Class primitiveClass) {
        return primitiveToWrapper.get(primitiveClass);
    }

    /**
     * 将查询结果封装为指定对象
     *
     * @param clazz        封装结果的类
     * @param simpleResult 查询结果
     *
     * @return 封装后的对象集合
     *
     * @throws Exception 如果封装失败
     */
    @SuppressWarnings({"unchecked"})
    public static List<Object> convert(
        Class clazz, List<Object> simpleResult, NameConverter nameConverter
    ) throws Exception {
        ArrayList<Object> result = new ArrayList<>();

        for (Object obj : simpleResult) {
            Map<String, Object> row = (Map<String, Object>) obj;
            Object converted = convertRow(clazz, row, nameConverter);
            result.add(converted);
        }

        return result;
    }

    /**
     * 将一条查询记录包装为一个对象
     *
     * @param clazz         包装类
     * @param row           查询记录
     * @param nameConverter 名称转换类
     *
     * @return 包装后的对象
     *
     * @throws IllegalAccessException    如果实例化包装类失败
     * @throws InstantiationException    如果实例化包装类失败
     * @throws NoSuchMethodException     如果实例化包装类失败
     * @throws InvocationTargetException 如果实例化包装类失败
     * @throws SQLException              如果读取 LOB 数据失败
     * @throws IOException               如果读取 LOB 数据失败
     */
    public static Object convertRow(
        Class<?> clazz, Map<String, Object> row, NameConverter nameConverter
    )
        throws IllegalAccessException, InstantiationException, SQLException, IOException,
        NoSuchMethodException, InvocationTargetException {

        if (clazz.isRecord()) {
            return convertRowRecord(clazz, row, nameConverter);
        }

        // pojo 类必须有一个缺省的构造函数。
        Object result = clazz.getDeclaredConstructor().newInstance();

        for (String colName : row.keySet()) {
            String fieldName = nameConverter.column2Field(colName);
            Field field = TypeUtil.getFieldIgnoreCase(clazz, fieldName);
            if (field == null) {
                warn(clazz.getCanonicalName() + "." + fieldName,
                    "Unable to convert column '" + colName + "' to field '" +
                        clazz.getCanonicalName() + "." + fieldName
                        + "': field not found");
                continue;
            }

            Object value = convertProperty(row.get(colName), field.getType());
            if (value != null) {
                try {
                    BeanUtil.setValueIgnoreCase(result, fieldName, value);
                } catch (Exception e) {
                    warn(
                        clazz.getCanonicalName() + "." + fieldName,
                        String.format("Error setting value of field '%s#%s' (%s)",
                            clazz.getCanonicalName(), fieldName, value.getClass().getCanonicalName()));
                }
            }
        }
        return result;
    }

    private static Object convertRowRecord(
        Class<?> clazz, Map<String, Object> row, NameConverter nameConverter
    ) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        var fields = clazz.getDeclaredFields();
        var constructorArgTypes = new Class[fields.length];
        var constructorArgValues = new Object[fields.length];

        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            constructorArgTypes[i] = field.getType();
            var column = nameConverter.field2Column(field.getName());
            var rawValue = row.getOrDefault(column, row.get(column.toUpperCase()));
            constructorArgValues[i] = convertProperty(rawValue, field.getType());
        }

        Constructor<?> constructor = clazz.getConstructor(constructorArgTypes);
        return constructor.newInstance(constructorArgValues);
    }

    /**
     * 根据属性类型转换值对象。这里是从 Row 对象的属性值转换为 Bean 对象的属性值
     * <ol>
     * <li>当值对象为 {@link Timestamp} 时，将其转换为 {@link java.util.Date} 对象；</li>
     * </ol>
     *
     * @param o         对象
     * @param fieldType 属性类型
     *
     * @return 转换后的值
     *
     */
    @SuppressWarnings("rawtypes")
    private static Object convertProperty(Object o, Class<?> fieldType)
        throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        if (fieldType == Boolean.TYPE) {
            String str = String.valueOf(o);
            if (str.matches("^-?\\d+\\.?\\d+$")) {    // 如果该字段存储的是数字
                return !"0".equals(str);
            } else {
                return "true".equalsIgnoreCase(str) || "yes".equalsIgnoreCase(str);
            }
        } else if (o == null) {
            return null;
        } else if (o instanceof Timestamp) {
            return new Date(((Timestamp) o).getTime());
        } else if (Number.class.isAssignableFrom(fieldType)) {
            // Assuming all subclasses of Number have a static constructor 'valueOf(String)'
            return fieldType.getMethod("valueOf", String.class).invoke(null, convertToString(o));
        } else if (fieldType == String.class) {
            return convertToString(o);
        } else if (fieldType.isEnum() && o instanceof String) {
            return Enum.valueOf((Class)fieldType, (String) o);
        } else {
            return o;
        }
    }

    private static String convertToString(Object o) {
        if (o instanceof Number) {
            return new BigDecimal(o.toString()).toString();
        } else {
            return o.toString();
        }
    }

    /**
     * 读取 lob 对象的值，并返回字符串
     *
     * @param lob 要读取的 Lob 对象
     *
     * @return 自字符串
     *
     * @throws SQLException 如果数据库访问 LOB 字段失败
     * @throws IOException  如果从流中读取内容失败
     */
    public static String readLobString(Object lob) throws SQLException, IOException {
        if (lob instanceof Clob) {
            return ClobUtil.read((Clob) lob);
        } else if (lob instanceof Blob) {
            return BlobReader.readString((Blob) lob, "Unicode");
        } else {
            LOG.warn("参数不是 lob 对象：" + lob.getClass());
            return "";
        }
    }

    /**
     * 打印警告信息。相同的警告只会打印一次。
     *
     * @param msg 警告信息
     */
    private static void warn(String type, String msg) {
        if (warnedTypes.contains(type)) {
            return;
        }

        synchronized (warnedTypes) {
            warnedTypes.add(type);
            LOG.warn(msg);
        }
    }
}

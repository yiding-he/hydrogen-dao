package com.hyd.dao.database.type;

import com.hyd.dao.log.Logger;
import com.hyd.dao.util.BeanUtil;
import com.hyd.dao.util.TypeUtil;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.*;
import java.util.Date;
import java.util.*;

/**
 * 将查询结果封装为 pojo 对象的类
 */
@SuppressWarnings({"unchecked"})
public class TypeConverter {

    static final Logger LOG = Logger.getLogger(TypeConverter.class);

    private static Map<String, String> convertBuffer = new HashMap<String, String>();

    private static ThreadLocal<List<String>> warnedMsgs = new ThreadLocal<List<String>>();

    static Map<Class, Class> primitiveToWrapper = new HashMap<Class, Class>();

    static Map<Class, Class> wrapperToPrimitive = new HashMap<Class, Class>();

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
        ArrayList<Object> result = new ArrayList<Object>();

        for (Object obj : simpleResult) {
            Map<String, Object> row = (Map<String, Object>) obj;
            Object converted = convertRow(clazz, row, nameConverter);
            result.add(converted);
        }

        warnedMsgs.set(new ArrayList<>());
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
            Class clazz, Map<String, Object> row, NameConverter nameConverter
    )
            throws IllegalAccessException, InstantiationException, SQLException, IOException,
            NoSuchMethodException, InvocationTargetException {

        // pojo 类必须有一个缺省的构造函数。
        Object result = clazz.getDeclaredConstructor().newInstance();

        for (String colName : row.keySet()) {
            String fieldName = nameConverter.column2Field(colName);
            Field field = TypeUtil.getFieldIgnoreCase(clazz, fieldName);
            if (field == null) {
                warn("Unable to convert column '" + colName + "' to field name.");
                continue;
            }

            Object value = convertProperty(row.get(colName), field.getType());
            if (value != null) {
                try {
                    BeanUtil.setValueIgnoreCase(result, fieldName, value);
                } catch (Exception e) {
                    warn(String.format("Error setting value of field '%s#%s' (%s)",
                            clazz.getCanonicalName(), fieldName, value.getClass().getCanonicalName()));
                }
            }
        }
        return result;
    }

    private static Class getFieldType(Class clazz, String fieldName) throws IllegalAccessException {
        Field field = TypeUtil.getFieldIgnoreCase(clazz, fieldName);
        if (field != null) {
            return field.getType();
        } else {
            throw new IllegalAccessException(
                    "Field '" + fieldName + "' not found in class " + clazz.getCanonicalName());
        }
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
     * @throws SQLException 如果数据库访问 LOB 字段失败
     * @throws IOException  如果从流中读取内容失败
     */
    private static Object convertProperty(Object o, Class fieldType) throws SQLException, IOException {

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
        } else if (fieldType == String.class) {
            return convertToString(o);
        } else if (fieldType.isEnum() && o instanceof String) {
            return Enum.valueOf(fieldType, (String) o);
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
    private static void warn(String msg) {
        List<String> list = warnedMsgs.get();
        if (list == null) {
            list = new ArrayList<>();
            warnedMsgs.set(list);
        }
        if (!list.contains(msg)) {
            LOG.warn(msg);
            list.add(msg);
        }
    }
}

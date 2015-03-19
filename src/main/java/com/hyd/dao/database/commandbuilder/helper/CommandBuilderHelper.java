package com.hyd.dao.database.commandbuilder.helper;

import com.hyd.dao.DAO;
import com.hyd.dao.DataConversionException;
import com.hyd.dao.Sequence;
import com.hyd.dao.database.ColumnInfo;
import com.hyd.dao.database.connection.ConnectionUtil;
import com.hyd.dao.util.BeanUtil;
import com.hyd.dao.util.LockFactory;
import com.hyd.dao.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用于构造 SQL 命令的帮助类，隐藏不同数据库之间的区别
 */
public class CommandBuilderHelper {

    // 缓存已经生成的字段info

    private static Map<String, ColumnInfo[]> cache = new HashMap<String, ColumnInfo[]>();

    static final Logger LOG = LoggerFactory.getLogger(CommandBuilderHelper.class);

    protected Connection connection;

    protected ColumnMeta columnMeta;

    /**
     * 构造函数
     *
     * @param connection 数据库连接
     */
    protected CommandBuilderHelper(Connection connection) {
        this.connection = connection;
        this.columnMeta = ColumnMeta.Oracle;
    }

    /**
     * 获取一个 CommandBuilderHelper 对象
     *
     * @param conn 数据库连接
     *
     * @return 根据数据库类型产生的 CommandBuilderHelper 对象
     *
     * @throws SQLException 如果获取数据库连接信息失败
     */
    public static CommandBuilderHelper getHelper(Connection conn) throws SQLException {
        if (ConnectionUtil.isOracle(conn)) {
            return new OracleCommandBuilderHelper(conn);
        } else if (ConnectionUtil.isMySql(conn)) {
            return new MySqlCommandBuilderHelper(conn);
        } else if (ConnectionUtil.isHsqlDB(conn)) {
            return new HSQLDBCommandBuildHelper(conn);
        } else {
            return new CommandBuilderHelper(conn);
        }
    }

    /**
     * 清除所有的表结构缓存
     */
    public static void clearTableCache() {
        cache.clear();
    }

    /**
     * 获得指定库表的字段信息
     *
     * @param schema    登录数据库的用户名
     * @param tableName 表名
     *
     * @return 表的字段信息
     *
     * @throws SQLException 如果获取信息失败
     */
    public ColumnInfo[] getColumnInfos(String schema, String tableName) throws SQLException {
        String fullTableName = schema + "." + tableName;

        if (cache.get(fullTableName) != null) {
            return cache.get(fullTableName);
        } else {
            synchronized (LockFactory.getLock(fullTableName)) {
                return getColumnInfos(schema, tableName, fullTableName);
            }
        }
    }

    // TODO 对不同的数据库读取方式可能不一样
    private ColumnInfo[] getColumnInfos(
            String schema, String tableName, String fullTableName) throws SQLException {

        if (cache.get(fullTableName) != null) {
            return cache.get(fullTableName);
        }

        LOG.debug("Reading columns of table {}...", fullTableName);

        String fixedSchema = schema.toUpperCase();
        String fixedTableName = fixTableName(tableName);

        DatabaseMetaData dbMeta = connection.getMetaData();
        ResultSet columns = dbMeta.getColumns(getCatalog(), getSchema(fixedSchema), getTableName(fixedTableName), "%");
        ResultSet keys = dbMeta.getPrimaryKeys(getCatalog(), getSchema(fixedSchema), getTableName(fixedTableName));

        List<String> keyNames = new ArrayList<String>();

        // COLUMN_NAME, DATA_TYPE, COLUMN_SIZE, NULLABLE, REMARKS 可以用于 Oracle 和 MySQL
        while (keys.next()) {
            keyNames.add(keys.getString(columnMeta.columnName));
        }

        List<ColumnInfo> infos = new ArrayList<ColumnInfo>();

        // COLUMN_NAME, DATA_TYPE, COLUMN_SIZE, NULLABLE, REMARKS 可以用于 Oracle 和 MySQL
        while (columns.next()) {
            ColumnInfo info = new ColumnInfo();
            String columnName = columns.getString(columnMeta.columnName);
            info.setColumnName(columnName);
            info.setDataType(Integer.parseInt(columns.getString(columnMeta.dataType)));
            info.setPrimary(keyNames.contains(columnName));
            info.setComment(columns.getString(columnMeta.remarks));
            info.setSize(Integer.parseInt(columns.getString(columnMeta.columnSize)));
            info.setNullable("1".equals(columns.getString(columnMeta.nullable)));
            infos.add(info);
        }

        ColumnInfo[] result = infos.toArray(new ColumnInfo[infos.size()]);
        cache.put(fullTableName, result);

        try {
            columns.close();
            keys.close();
        } catch (SQLException e) {
            LOG.error("", e);
        }
        return result;
    }

    protected String getTableName(String tableName) {
        return tableName;
    }

    protected String getSchema(String schema) {
        return schema;
    }

    protected String getCatalog() throws SQLException {
        return connection.getCatalog();
    }

    protected String fixTableName(String tableName) {
        return tableName;
    }

    /**
     * 生成 SQL 语句参数
     *
     * @param infos  字段信息
     * @param object 提供字段的参数值的对象
     *
     * @return 生成的 SQL 语句参数
     */
    public static List generateParams(ColumnInfo[] infos, Object object) {
        List<Object> params = new ArrayList<Object>();
        for (ColumnInfo info : infos) {
            if (info.getDataType() != DAO.SYSDATE_TYPE) {
                params.add(generateParamValue(object, info));
            }
        }
        return params;
    }

    /**
     * 根据字段信息，从对象中取得相应的属性值
     *
     * @param object 对象
     * @param info   字段信息
     *
     * @return 属性值。如果获取失败或需要跳过该字段则返回 null
     */
    public static Object generateParamValue(Object object, ColumnInfo info) {
        String fieldName = StringUtil.columnToProperty(info.getColumnName());

        String strValue;
        Object value;

        // 如果 object 是一个 Map，则根据字段名取值；否则根据属性名取值。
        if (object instanceof Map) {
            Map map = (Map) object;
            value = map.get(info.getColumnName().toLowerCase());
            if (value == null) {
                value = map.get(info.getColumnName().toUpperCase());
            }
            if (value == null) {
                return null;
            }
            strValue = StringUtil.valueOf(value);
        } else {

            Field field;
            try {
                field = object.getClass().getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                String className = object.getClass().getCanonicalName();
                LOG.debug("Property '" + fieldName + "' not exists for class " + className, e);
                return null;
            }

            // 判断属性是否被标记了 @Sequencce
            if (isAnnotatedWithSequencce(field)) {
                info.setAutoIncrement(true);

                String sequenceName = field.getAnnotation(Sequence.class).sequenceName();
                info.setSequenceName(sequenceName);
                return null;
            }

            value = BeanUtil.getValue(object, fieldName);

            if (value == null) {
                return null;
            }
            strValue = StringUtil.valueOf(value);
        }

        // 获取返回值
        switch (info.getDataType()) {
            case Types.NUMERIC:     // 1. 如果是数字类型的字段，则根据 strValue 进行转换；
            case Types.DECIMAL:
            case Types.BIGINT:
            case Types.DOUBLE:
            case Types.FLOAT:
            case Types.INTEGER:
                if (StringUtil.isEmptyString(strValue)) {
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

    private static boolean isAnnotatedWithSequencce(Field field) {
        return field.isAnnotationPresent(Sequence.class);
    }

    /**
     * Fix column name in the sql statement.
     *
     * @param column column name
     *
     * @return fixed column name
     *
     * @throws SQLException when fails
     */
    public String getColumnName(String column) throws SQLException {
        return column;
    }

    public String getSysdateMark() {
        return "CURRENT_TIMESTAMP";
    }
}

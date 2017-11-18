package com.hyd.dao.database.commandbuilder.helper;

import com.hyd.dao.DAO;
import com.hyd.dao.DAOException;
import com.hyd.dao.DataConversionException;
import com.hyd.dao.Sequence;
import com.hyd.dao.database.ColumnInfo;
import com.hyd.dao.database.connection.ConnectionUtil;
import com.hyd.dao.log.Logger;
import com.hyd.dao.util.BeanUtil;
import com.hyd.dao.util.Locker;
import com.hyd.dao.util.Str;

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

    private static final Logger LOG = Logger.getLogger(CommandBuilderHelper.class);

    // 缓存已经生成的字段info
    private static Map<String, ColumnInfo[]> cache = new HashMap<>();

    protected Connection connection;

    /**
     * 构造函数
     *
     * @param connection 数据库连接
     */
    protected CommandBuilderHelper(Connection connection) {
        this.connection = connection;
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
        } else if (ConnectionUtil.isSqlServer(conn)) {
            return new SQLServerCommandBuilderHelper(conn);
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
            return Locker.lockAndRun(fullTableName, () -> getColumnInfos(schema, tableName, fullTableName));
        }
    }

    private ColumnInfo[] getColumnInfos(
            String schema, String tableName, String fullTableName) {
        try {
            return getColumnInfos0(schema, tableName, fullTableName);
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    private ColumnInfo[] getColumnInfos0(
            String schema, String tableName, String fullTableName) throws SQLException {

        if (cache.get(fullTableName) != null) {
            return cache.get(fullTableName);
        }

        LOG.debug("Reading columns of table " + fullTableName + "...");

        String fixedSchema = schema.toUpperCase();
        String fixedTableName = getTableNameForMeta(tableName);

        ColumnMeta columnMeta = getColumnMeta();
        DatabaseMetaData dbMeta = connection.getMetaData();
        ResultSet columns = dbMeta.getColumns(getCatalog(), getSchema(fixedSchema), fixedTableName, "%");

        List<String> keyNames = getPrimaryKeyColumns(fixedSchema, fixedTableName, dbMeta);

        List<ColumnInfo> infos = new ArrayList<ColumnInfo>();

        while (columns.next()) {

            String columnName = columns.getString(columnMeta.columnName);
            String typeName = columns.getString(columnMeta.typeName);
            boolean primaryKey = isPrimaryKey(typeName, keyNames, columnName);

            ColumnInfo info = new ColumnInfo();
            info.setColumnName(columnName);
            info.setDataType(Integer.parseInt(columns.getString(columnMeta.dataType)));
            info.setPrimary(primaryKey);
            info.setComment(columns.getString(columnMeta.remarks));
            info.setSize(Integer.parseInt(columns.getString(columnMeta.columnSize)));
            info.setNullable("1".equals(columns.getString(columnMeta.nullable)));
            infos.add(info);
        }

        ColumnInfo[] result = infos.toArray(new ColumnInfo[infos.size()]);
        cache.put(fullTableName, result);

        try {
            columns.close();
        } catch (SQLException e) {
            LOG.error("", e);
        }
        return result;
    }

    private List<String> getPrimaryKeyColumns(
            String fixedSchema, String fixedTableName, DatabaseMetaData dbMeta) throws SQLException {

        ColumnMeta columnMeta = getColumnMeta();
        List<String> keyNames = new ArrayList<>();

        try (ResultSet keys = dbMeta.getPrimaryKeys(getCatalog(), getSchema(fixedSchema), fixedTableName)) {
            while (keys.next()) {
                keyNames.add(keys.getString(columnMeta.columnName));
            }
        }

        return keyNames;
    }

    protected ColumnMeta getColumnMeta() {
        return ColumnMeta.Oracle;
    }

    private boolean isPrimaryKey(String typeName, List<String> keyNames, String columnName) {
        return (typeName != null && typeName.toLowerCase().contains("identity"))
                || keyNames.contains(columnName);
    }

    protected String getSchema(String schema) {
        return schema;
    }

    protected String getCatalog() throws SQLException {
        return connection.getCatalog();
    }

    // 当查询 meta 数据需要时，修正表名
    protected String getTableNameForMeta(String tableName) {
        return tableName;
    }

    // 当组合 SQL 语句需要时，修正表名
    public String getTableNameForSql(String tableName) {
        return tableName;
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
    public String getColumnNameForSql(String column) throws SQLException {
        return column;
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
        String fieldName = Str.columnToProperty(info.getColumnName());

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
            strValue = Str.valueOf(value);
        } else {

            Field field;
            try {
                field = object.getClass().getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                // 从表字段名查询对象属性失败
                return null;
            }

            // 判断属性是否被标记了 @Sequence
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
            strValue = Str.valueOf(value);
        }

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

    private static boolean isAnnotatedWithSequencce(Field field) {
        return field.isAnnotationPresent(Sequence.class);
    }

    public String getSysdateMark() {
        return "CURRENT_TIMESTAMP";
    }

    // 根据当前的 SQL 语句生成带查询范围的语句
    public String getRangedSql(String sql, int startPos, int endPos) {
        return null;
    }

    // 根据当前的 SQL 语句生成返回查询结果数量的语句
    public String getCountSql(String sql) {
        return "select count(*) cnt from (" + sql + ")";
    }
}

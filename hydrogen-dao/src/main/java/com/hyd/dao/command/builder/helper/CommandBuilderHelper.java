package com.hyd.dao.command.builder.helper;

import com.hyd.dao.DAO;
import com.hyd.dao.DAOException;
import com.hyd.dao.database.ColumnInfo;
import com.hyd.dao.database.DatabaseType;
import com.hyd.dao.database.FQN;
import com.hyd.dao.database.type.NameConverter;
import com.hyd.dao.exception.DataConversionException;
import com.hyd.dao.log.Logger;
import com.hyd.dao.mate.util.BeanUtil;
import com.hyd.dao.mate.util.Cls;
import com.hyd.dao.mate.util.ConnectionContext;
import com.hyd.dao.mate.util.Str;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 用于构造 SQL 命令的帮助类，隐藏不同数据库之间的区别
 */
public class CommandBuilderHelper {

    private static final Logger LOG = Logger.getLogger(CommandBuilderHelper.class);

    // 缓存已经生成的字段info
    private static final Map<String, ColumnInfo[]> cache = new ConcurrentHashMap<>();

    private static final Map<DatabaseType, SqlFix> REGISTRY = new HashMap<>();

    static {
        REGISTRY.put(DatabaseType.Oracle, new OracleSqlFix());
        REGISTRY.put(DatabaseType.HSQLDB, new HsqldbSqlFix());
        REGISTRY.put(DatabaseType.MySQL, new MySqlSqlFix());
        REGISTRY.put(DatabaseType.SQLServer, new SQLServerSqlFix());
        REGISTRY.put(DatabaseType.Others, SqlFix.DEFAULT);
    }

    private final ConnectionContext context;

    private final DatabaseType databaseType;

    private final SqlFix sqlFix;

    public CommandBuilderHelper(ConnectionContext context) {
        this.context = context;
        this.databaseType = DatabaseType.of(context.getConnection());
        this.sqlFix = REGISTRY.getOrDefault(databaseType, SqlFix.DEFAULT);
    }

    public DatabaseType getDatabaseType() {
        return databaseType;
    }

    /**
     * 获取一个 CommandBuilderHelper 对象
     *
     * @return 根据数据库类型产生的 CommandBuilderHelper 对象
     *
     * @throws DAOException 如果获取数据库连接信息失败
     */
    public static CommandBuilderHelper getHelper(ConnectionContext context) throws DAOException {
        return new CommandBuilderHelper(context);
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
     * @param fqn 表名信息
     *
     * @return 表的字段信息
     */
    public ColumnInfo[] getColumnInfos(FQN fqn) {
        String strictName = fqn.getStrictName();

        return cache.computeIfAbsent(strictName, any -> {
            try {
                return getColumnInfos0(fqn);
            } catch (SQLException e) {
                throw new DAOException(e);
            }
        });
    }

    private ColumnInfo[] getColumnInfos0(FQN fqn) throws SQLException {

        String strictName = fqn.getStrictName();

        LOG.debug("Reading columns of table " + strictName + "...");

        String fixedSchema = sqlFix.getSchema(fqn.getSchema());
        String fixedTableName = sqlFix.getTableNameForMeta(fqn.getName());

        Connection connection = this.context.getConnection();
        ColumnMeta columnMeta = sqlFix.getColumnMeta();
        DatabaseMetaData dbMeta = connection.getMetaData();

        try (ResultSet columns = dbMeta.getColumns(
            sqlFix.getCatalog(connection), sqlFix.getSchema(fixedSchema), fixedTableName, "%")) {

            List<String> keyNames = getPrimaryKeyColumns(fixedSchema, fixedTableName, dbMeta);
            List<ColumnInfo> infos = new ArrayList<>();

            while (columns.next()) {
                String columnName = columns.getString(columnMeta.columnName);
                String typeName = columns.getString(columnMeta.typeName);
                boolean primaryKey = isPrimaryKey(typeName, keyNames, columnName);

                ColumnInfo info = new ColumnInfo();
                info.setColumnName(columnName);
                info.setDataType(Integer.parseInt(columns.getString(columnMeta.dataType)));
                info.setPrimary(primaryKey);
                info.setComment(columns.getString(columnMeta.remarks));
                info.setSize(columns.getInt(columnMeta.columnSize));
                info.setNullable("1".equals(columns.getString(columnMeta.nullable)));
                infos.add(info);
            }

            return infos.toArray(new ColumnInfo[0]);
        }
    }

    private List<String> getPrimaryKeyColumns(
        String fixedSchema, String fixedTableName, DatabaseMetaData dbMeta) throws SQLException {

        ColumnMeta columnMeta = sqlFix.getColumnMeta();
        List<String> keyNames = new ArrayList<>();

        try (ResultSet keys = dbMeta.getPrimaryKeys(
            sqlFix.getCatalog(this.context.getConnection()),
            sqlFix.getSchema(fixedSchema), fixedTableName
        )) {
            while (keys.next()) {
                keyNames.add(keys.getString(columnMeta.columnName));
            }
        }

        return keyNames;
    }

    private boolean isPrimaryKey(String typeName, List<String> keyNames, String columnName) {
        return (typeName != null && typeName.toLowerCase().contains("identity"))
            || keyNames.contains(columnName);
    }

    /**
     * 生成 SQL 语句参数
     *
     * @param infos  字段信息
     * @param object 提供字段的参数值的对象
     *
     * @return 生成的 SQL 语句参数
     */
    public List<Object> generateParams(ColumnInfo[] infos, Object object) {
        List<Object> params = new ArrayList<>();
        for (ColumnInfo info : infos) {
            if (info.getDataType() != DAO.SYSDATE_TYPE) {
                params.add(generateParamValue(object, info));
            }
        }
        return params;
    }

    /**
     * 根据 bean 类型过滤字段列表，删除类型中没有定义的字段
     */
    public ColumnInfo[] filterColumnsByType(ColumnInfo[] original, Class<?> type) {
        if (!Map.class.isAssignableFrom(type)) {
            List<ColumnInfo> infoList = new ArrayList<>();
            for (ColumnInfo info : original) {
                String field = context.getNameConverter().column2Field(info.getColumnName());
                if (Cls.hasField(type, field)) {
                    infoList.add(info);
                }
            }
            return infoList.toArray(new ColumnInfo[0]);
        } else {
            return Arrays.copyOf(original, original.length);
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
    public Object generateParamValue(Object object, ColumnInfo info) {
        final NameConverter nameConverter = this.context.getNameConverter();
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

    public String getTableNameForSql(String tableName) {
        return sqlFix.getTableNameForSql(tableName);
    }

    public String getSysdateMark() {
        return sqlFix.getSysdateMark();
    }

    public String getStrictName(String name) {
        return name.equals("%") ? name : sqlFix.getStrictName(name);
    }

    public String getRangedSql(String sql, int startPos, int endPos) {
        return sqlFix.getRangedSql(sql, startPos, endPos);
    }

    public String getCountSql(String sql) {
        return sqlFix.getCountSql(sql);
    }
}

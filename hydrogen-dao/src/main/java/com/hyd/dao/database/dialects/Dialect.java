package com.hyd.dao.database.dialects;

import com.hyd.dao.database.ColumnInfo;
import com.hyd.dao.database.FQN;
import com.hyd.dao.mate.util.Str;
import com.hyd.dao.mate.util.TypeUtil;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface Dialect {

    /**
     * 当查询元数据时，要以什么方式转换对象名
     * 某些数据库例如 H2，创建表时，表名可以用小写，但创建出来的表是大写的
     */
    enum MetaNameConvention {
        Uppercase, Lowercase, Unchanged
    }

    interface ColumnMetaFields {

        String columnNameField();

        String columnSizeField();

        String nullableField();

        String dataTypeField();

        String typeNameField();

        String remarksField();
    }

    //////////////////////////////////////////////////////////////

    Predicate<Connection> getMatcher();

    String wrapRangeQuery(String sql, int startPos, int endPos);

    default String wrapCountQuery(String sql) {
        return "select count(*) cnt from (" + sql + ")";
    }

    default ColumnMetaFields getColumnMetaFields() {
        return new ColumnMetaFields() {
            @Override
            public String columnNameField() {
                return "COLUMN_NAME";
            }

            @Override
            public String columnSizeField() {
                return "COLUMN_SIZE";
            }

            @Override
            public String nullableField() {
                return "NULLABLE";
            }

            @Override
            public String dataTypeField() {
                return "DATA_TYPE";
            }

            @Override
            public String typeNameField() {
                return "TYPE_NAME";
            }

            @Override
            public String remarksField() {
                return "REMARKS";
            }
        };
    }

    default String currentTimeExpression() {
        return "current_timestamp";  // SQL 92 spec
    }

    default String identityQuoter() {
        return "\"";
    }

    default String quote(String schema, String name) {
        return quote((Str.isEmpty(schema) ? "" : schema + ".") + name);
    }

    default String quote(String objectName) {
        return Stream.of(objectName.split("\\."))
            .map(n -> identityQuoter() + n + identityQuoter())
            .collect(Collectors.joining("."));
    }

    default Object parseCallableStatementResult(int sqlType, Object value) {
        return TypeUtil.convertDatabaseValue(sqlType, value);
    }

    default int resultSetTypeForReading() {
        return ResultSet.TYPE_FORWARD_ONLY;
    }

    default String getJavaType(ColumnInfo columnInfo) {

        int dataType = columnInfo.getDataType();
        int size = columnInfo.getSize();

        switch (dataType) {
            case Types.VARCHAR:
            case Types.CHAR:
            case Types.LONGVARCHAR:
                return "String";
            case Types.BIT:
                return "Boolean";
            case Types.NUMERIC:
                return "BigDecimal";
            case Types.TINYINT:
                return "Integer";
            case Types.SMALLINT:
                return "Short";
            case Types.INTEGER:
                return size < 10 ? "Integer" : "Long";
            case Types.BIGINT:
                return "Long";
            case Types.REAL:
            case Types.FLOAT:
                return "Float";
            case Types.DOUBLE:
                return "Double";
            case Types.VARBINARY:
            case Types.BINARY:
                return "byte[]";
            case Types.DATE:
            case Types.TIME:
            case Types.TIMESTAMP:
                return "Date";
            default:
                return getJavaTypeByDatabase(columnInfo);
        }
    }

    default String getJavaTypeByDatabase(ColumnInfo columnInfo) {
        return "String";
    }

    default MetaNameConvention getMetaNameConvention() {
        return MetaNameConvention.Unchanged;
    }

    default String fixMetaName(String metaName) {
        switch (getMetaNameConvention()) {
            case Lowercase:
                return metaName.toLowerCase();
            case Uppercase:
                return metaName.toUpperCase();
            case Unchanged:
                return metaName;
        }
        throw new RuntimeException("Shouldn't be here");
    }

    default String fixCatalog(String connectionCatalog, FQN fqn) {
        return null;
    }
}

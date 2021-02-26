package com.hyd.dao.command.builder.helper;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.hyd.dao.DAOException;
import com.hyd.dao.database.ColumnInfo;
import com.hyd.dao.database.FQN;
import com.hyd.dao.database.dialects.Dialect;
import com.hyd.dao.database.dialects.Dialects;
import com.hyd.dao.log.Logger;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class ColumnInfoHelper {

    private static final Logger log = Logger.getLogger(ColumnInfoHelper.class);

    public static final String NULLABLE = "1";

    /**
     * 缓存表的列信息。当数据库变更后，需要清空缓存，或等待缓存超时
     */
    private static Cache<FQN, List<ColumnInfo>> columnInfoCache = Caffeine.newBuilder()
        .maximumSize(1000)
        .expireAfterAccess(Duration.ofHours(1))
        .build();

    /**
     * 以自定义配置来初始化缓存
     */
    public static synchronized void resetColumnInfoCache(Caffeine<FQN, List<ColumnInfo>> builder) {
        columnInfoCache = builder.build();
    }

    /**
     * 清空缓存。当数据库变更后若不想重启应用，则调用此方法。
     */
    public static synchronized void cleanUpCache() {
        columnInfoCache.cleanUp();
    }

    public static List<ColumnInfo> getColumnInfo(FQN fqn, Connection connection) {
        return columnInfoCache.get(fqn, _fqn -> {
            log.debug("Reading columns of table " + fqn + "...");

            try {
                Dialect dialect = Dialects.getDialect(connection);

                Dialect.ColumnMetaFields columnMeta = dialect.getColumnMetaFields();
                DatabaseMetaData dbMeta = connection.getMetaData();

                String catalog = dialect.fixCatalog(connection.getCatalog(), fqn);
                String schema = fqn.getSchema();
                String fixedName = dialect.fixMetaName(fqn.getName());

                try (
                    ResultSet columns = dbMeta.getColumns(catalog, schema, fixedName, "%");
                    ResultSet keys = dbMeta.getPrimaryKeys(catalog, schema, fixedName)
                ) {

                    List<String> keyNames = new ArrayList<>();
                    while (keys.next()) {
                        keyNames.add(keys.getString(columnMeta.columnNameField()));
                    }

                    List<ColumnInfo> infos = new ArrayList<>();
                    while (columns.next()) {
                        String columnName = columns.getString(columnMeta.columnNameField());
                        boolean primaryKey = keyNames.contains(columnName);

                        ColumnInfo info = new ColumnInfo();
                        info.setColumnName(columnName);
                        info.setDataType(Integer.parseInt(columns.getString(columnMeta.dataTypeField())));
                        info.setPrimary(primaryKey);
                        info.setComment(columns.getString(columnMeta.remarksField()));
                        info.setSize(columns.getInt(columnMeta.columnSizeField()));
                        info.setNullable(NULLABLE.equals(columns.getString(columnMeta.nullableField())));
                        infos.add(info);
                    }

                    return infos;
                }
            } catch (SQLException e) {
                throw new DAOException(e);
            }
        });
    }
}

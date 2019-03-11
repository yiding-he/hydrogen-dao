package com.hyd.dao.src.code;

import com.hyd.dao.database.ColumnInfo;
import com.hyd.dao.database.DatabaseType;
import com.hyd.dao.database.type.NameConverter;
import com.hyd.dao.util.TypeUtil;

/**
 * (description)
 * created at 2018/4/12
 *
 * @author yidin
 */
public abstract class ClassDefBuilder {

    protected String packageName;

    protected String tableName;

    protected ColumnInfo[] columnInfos;

    protected DatabaseType databaseType;

    protected NameConverter nameConverter;

    public ClassDefBuilder(
            String packageName, String tableName, ColumnInfo[] columnInfos,
            DatabaseType databaseType, NameConverter nameConverter
    ) {
        this.packageName = packageName;
        this.tableName = tableName;
        this.columnInfos = columnInfos;
        this.databaseType = databaseType;
        this.nameConverter = nameConverter;
    }

    public abstract ClassDef build(String tableName);

    protected String getJavaType(ColumnInfo columnInfo) {
        return TypeUtil.getJavaType(databaseType, columnInfo);
    }
}

package com.hyd.dao.src.code;

import com.hyd.dao.database.ColumnInfo;
import com.hyd.dao.database.DatabaseType;
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

    public ClassDefBuilder(
            String packageName, String tableName, ColumnInfo[] columnInfos, DatabaseType databaseType) {
        this.packageName = packageName;
        this.tableName = tableName;
        this.columnInfos = columnInfos;
        this.databaseType = databaseType;
    }

    public abstract ClassDef build(String tableName);

    protected String getJavaType(int type) {
        return TypeUtil.getJavaType(databaseType, type);
    }
}

package com.hyd.dao.src.code;

import com.hyd.dao.database.ColumnInfo;
import com.hyd.dao.database.DatabaseType;
import com.hyd.dao.util.Str;

/**
 * (description)
 * created at 2018/4/12
 *
 * @author yidin
 */
public class ModelClassBuilder extends ClassDefBuilder {

    public ModelClassBuilder(
            String tableName, ColumnInfo[] columnInfos, DatabaseType databaseType) {

        super(tableName, columnInfos, databaseType);
    }

    @Override
    public ClassDef build(String tableName) {

        ClassDef classDef = new ClassDef();
        classDef.imports = new ImportDef("java.util.Date");
        classDef.className = Str.underscore2Class(tableName);

        for (ColumnInfo columnInfo : columnInfos) {
            FieldDef field = new FieldDef();
            field.name = Str.uncapitalize(Str.underscore2Property(columnInfo.getColumnName()));
            field.type = getJavaType(columnInfo.getDataType());
            field.access = AccessType.Private;

            classDef.addFieldIfNotExists(field);
            classDef.addMethod(field.toGetterMethod());
            classDef.addMethod(field.toSetterMethod());
        }

        return classDef;
    }

}

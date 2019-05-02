package com.hyd.dao.src.code;

import com.hyd.dao.database.ColumnInfo;
import com.hyd.dao.database.DatabaseType;
import com.hyd.dao.database.type.NameConverter;
import com.hyd.dao.util.Str;

/**
 * (description)
 * created at 2018/4/12
 *
 * @author yidin
 */
public class ModelClassBuilder extends ClassDefBuilder {

    public ModelClassBuilder(
            String packageName, String tableName, ColumnInfo[] columnInfos,
            DatabaseType databaseType, NameConverter nameConverter) {
        super(packageName, tableName, columnInfos, databaseType, nameConverter);
    }

    private boolean settersEnabled = true;

    private boolean gettersEnabled = true;

    public void setSettersEnabled(boolean settersEnabled) {
        this.settersEnabled = settersEnabled;
    }

    public void setGettersEnabled(boolean gettersEnabled) {
        this.gettersEnabled = gettersEnabled;
    }

    @Override
    public ClassDef build(String tableName) {

        ClassDef classDef = new ClassDef();
        classDef.imports = new ImportDef("java.util.Date").addAll(this.imports);
        classDef.className = Str.underscore2Class(tableName);
        classDef.annotations.addAll(this.annotations);

        if (!Str.isEmptyString(packageName)) {
            classDef.packageDef = new PackageDef(packageName);
        }

        for (ColumnInfo columnInfo : columnInfos) {
            FieldDef field = new FieldDef();
            field.name = nameConverter.column2Field(columnInfo.getColumnName());
            field.type = getJavaType(columnInfo);
            field.access = AccessType.Private;

            classDef.addFieldIfNotExists(field);
            if (gettersEnabled) {
                classDef.addMethod(field.toGetterMethod());
            }
            if (settersEnabled) {
                classDef.addMethod(field.toSetterMethod());
            }
        }

        return classDef;
    }

}

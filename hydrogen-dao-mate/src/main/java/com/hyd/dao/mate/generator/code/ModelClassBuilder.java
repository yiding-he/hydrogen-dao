package com.hyd.dao.mate.generator.code;

import com.hyd.dao.database.ColumnInfo;
import com.hyd.dao.database.DatabaseType;
import com.hyd.dao.database.type.NameConverter;
import com.hyd.dao.mate.util.Str;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * (description)
 * created at 2018/4/12
 *
 * @author yidin
 */
public class ModelClassBuilder extends ClassDefBuilder {

    private boolean settersEnabled = true;

    private boolean gettersEnabled = true;

    /**
     * 每个 field 创建后的额外处理
     */
    private final List<BiConsumer<ColumnInfo, FieldDef>> afterFieldListeners = new ArrayList<>();

    public ModelClassBuilder(
            String packageName, String tableName, ColumnInfo[] columnInfos,
            DatabaseType databaseType, NameConverter nameConverter) {
        super(packageName, tableName, columnInfos, databaseType, nameConverter);
    }

    public void setSettersEnabled(boolean settersEnabled) {
        this.settersEnabled = settersEnabled;
    }

    public void setGettersEnabled(boolean gettersEnabled) {
        this.gettersEnabled = gettersEnabled;
    }

    public void addAfterFieldListener(BiConsumer<ColumnInfo, FieldDef> afterFieldListener) {
        this.afterFieldListeners.add(afterFieldListener);
    }

    @Override
    public ClassDef build(String tableName) {

        ClassDef classDef = new ClassDef();
        classDef.setImports(new ImportDef("java.util.Date").addAll(this.imports));
        classDef.setClassName(Str.underscore2Class(tableName));
        classDef.addAnnotations(this.annotations);

        if (!Str.isEmptyString(packageName)) {
            classDef.packageDef = new PackageDef(packageName);
        }

        for (ColumnInfo columnInfo : columnInfos) {
            FieldDef field = new FieldDef();
            field.name = nameConverter.column2Field(columnInfo.getColumnName());
            field.type = getJavaType(columnInfo);
            field.access = AccessType.Private;

            classDef.addFieldIfNotExists(field);

            for (BiConsumer<ColumnInfo, FieldDef> listener : afterFieldListeners) {
                listener.accept(columnInfo, field);
            }

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

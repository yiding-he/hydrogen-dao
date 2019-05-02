package com.hyd.dao.src.code;

import com.hyd.dao.database.ColumnInfo;
import com.hyd.dao.database.DatabaseType;
import com.hyd.dao.database.type.NameConverter;
import com.hyd.dao.util.TypeUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    protected List<AnnotationDef> annotations = new ArrayList<>();

    protected List<String> imports = new ArrayList<>();

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

    public void addImports(String... imports) {
        this.imports.addAll(Arrays.asList(imports));
    }

    public AnnotationDef addAnnotation(String name) {
        return addAnnotation(new AnnotationDef(name));
    }

    public AnnotationDef addAnnotation(AnnotationDef annotationDef) {
        this.annotations.add(annotationDef);
        return annotationDef;
    }

    public abstract ClassDef build(String tableName);

    protected String getJavaType(ColumnInfo columnInfo) {
        return TypeUtil.getJavaType(databaseType, columnInfo);
    }
}

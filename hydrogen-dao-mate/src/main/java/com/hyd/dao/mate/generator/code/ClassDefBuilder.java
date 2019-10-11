package com.hyd.dao.mate.generator.code;

import com.hyd.dao.database.ColumnInfo;
import com.hyd.dao.database.DatabaseType;
import com.hyd.dao.database.type.NameConverter;
import com.hyd.dao.mate.util.TypeUtil;
import java.util.*;
import java.util.function.Consumer;

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

    protected List<Consumer<ClassDefBuilder>> beforeClassListeners = new ArrayList<>();

    protected List<Consumer<ClassDef>> afterClassListeners = new ArrayList<>();

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

    public String getTableName() {
        return tableName;
    }

    //////////////////////////////////////////////////////////////

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

    public ClassDefBuilder addBeforeClassListener(Consumer<ClassDefBuilder> beforeListener) {
        this.beforeClassListeners.add(beforeListener);
        return this;
    }

    public ClassDefBuilder addAfterClassListener(Consumer<ClassDef> afterListener) {
        this.afterClassListeners.add(afterListener);
        return this;
    }

    //////////////////////////////////////////////////////////////

    public ClassDef buildClassDef(String tableName) {
        for (Consumer<ClassDefBuilder> listener : beforeClassListeners) {
            listener.accept(this);
        }

        ClassDef classDef = build(tableName);

        for (Consumer<ClassDef> listener : afterClassListeners) {
            listener.accept(classDef);
        }

        return classDef;
    }

    protected abstract ClassDef build(String tableName);

    protected String getJavaType(ColumnInfo columnInfo) {
        return TypeUtil.getJavaType(databaseType, columnInfo);
    }
}

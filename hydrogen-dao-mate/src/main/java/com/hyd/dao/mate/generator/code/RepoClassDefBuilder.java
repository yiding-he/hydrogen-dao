package com.hyd.dao.mate.generator.code;

import com.hyd.dao.database.ColumnInfo;
import com.hyd.dao.database.dialects.Dialect;
import com.hyd.dao.database.type.NameConverter;
import com.hyd.dao.mate.util.Str;

import java.util.List;

/**
 * (description)
 * created at 2018/4/18
 *
 * @author yidin
 */
public class RepoClassDefBuilder extends ClassDefBuilder {

    private final String modelPackage;

    public RepoClassDefBuilder(
        String repoPackage, String modelPackage, String tableName, List<ColumnInfo> columnInfos,
        Dialect dialect, NameConverter nameConverter) {
        super(repoPackage, tableName, columnInfos, dialect, nameConverter);
        this.modelPackage = modelPackage;
    }

    @Override
    public ClassDef build(String tableName) {
        String modelClassName = Str.underscore2Class(tableName);
        String className = modelClassName + "Repository";

        ClassDef classDef = new ClassDef();
        classDef.addAnnotation(new AnnotationDef("Repository"));
        classDef.className = className;
        classDef.imports = new ImportDef(
            "org.springframework.beans.factory.annotation.Autowired",
            "org.springframework.stereotype.Repository",
            "com.hyd.dao.DAO",
            "com.hyd.dao.SQL",
            "com.hyd.dao.Page",
            "java.math.BigDecimal",
            "java.util.Date",
            "java.util.Map",
            "java.util.List")
            .addAll(this.imports);

        if (!Str.isEmptyString(packageName)) {
            classDef.packageDef = new PackageDef(packageName);
        }

        if (!Str.isEmptyString(modelPackage)) {
            classDef.imports.add(modelPackage + "." + modelClassName);
        }

        FieldDef daoField = new FieldDef();
        daoField.access = AccessType.Private;
        daoField.name = "dao";
        daoField.type = "DAO";
        daoField.addAnnotation("Autowired");
        classDef.addFieldIfNotExists(daoField);

        classDef.addMethod(daoField.toSetterMethod());

        return classDef;
    }
}

package com.hyd.dao.src.code;

import com.hyd.dao.database.ColumnInfo;
import com.hyd.dao.database.DatabaseType;
import com.hyd.dao.util.Str;

/**
 * (description)
 * created at 2018/4/18
 *
 * @author yidin
 */
public class RepoClassDefBuilder extends ClassDefBuilder {

    public RepoClassDefBuilder(
            String tableName, ColumnInfo[] columnInfos,
            DatabaseType databaseType) {
        super(tableName, columnInfos, databaseType);
    }

    @Override
    public ClassDef build(String tableName) {
        String className = Str.underscore2Class(tableName) + "Repository";

        ClassDef classDef = new ClassDef();
        classDef.annotation = new AnnotationDef("Repository");
        classDef.className = className;
        classDef.imports = new ImportDef(
                "org.springframework.beans.factory.annotation.Autowired",
                "org.springframework.stereotype.Repository",
                "com.hyd.dao.DAO",
                "com.hyd.dao.SQL",
                "com.hyd.dao.Page",
                "java.math.BigDecimal",
                "java.util.Date",
                "java.util.List");

        FieldDef daoField = new FieldDef();
        daoField.access = AccessType.Private;
        daoField.name = "dao";
        daoField.type = "DAO";
        daoField.annotation = new AnnotationDef("Autowired");
        classDef.addFieldIfNotExists(daoField);

        return classDef;
    }
}

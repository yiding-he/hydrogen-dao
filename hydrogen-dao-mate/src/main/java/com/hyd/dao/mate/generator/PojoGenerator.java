package com.hyd.dao.mate.generator;

import com.hyd.dao.command.builder.helper.CommandBuilderHelper;
import com.hyd.dao.database.ColumnInfo;
import com.hyd.dao.database.DatabaseType;
import com.hyd.dao.database.executor.ExecutionContext;
import com.hyd.dao.database.type.NameConverter;
import com.hyd.dao.mate.generator.code.AnnotationDef;
import com.hyd.dao.mate.generator.code.ClassDef;
import com.hyd.dao.mate.generator.code.ModelClassBuilder;

import java.sql.Connection;
import java.sql.SQLException;

public class PojoGenerator {

    private Connection connection;

    private String pojoName;

    private String catalog;

    private String tableName;

    private NameConverter nameConverter;

    private boolean useLombok;

    private boolean useMybatisPlus;

    public void setUseMybatisPlus(boolean useMybatisPlus) {
        this.useMybatisPlus = useMybatisPlus;
    }

    public void setUseLombok(boolean useLombok) {
        this.useLombok = useLombok;
    }

    public void setNameConverter(NameConverter nameConverter) {
        this.nameConverter = nameConverter;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public void setPojoName(String pojoName) {
        this.pojoName = pojoName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    //////////////////////////////////////////////////////////////

    public String generateCode() throws SQLException {
        ExecutionContext context = new ExecutionContext();
        context.setDataSourceName("");
        context.setConnection(this.connection);
        context.setNameConverter(nameConverter);

        CommandBuilderHelper helper = CommandBuilderHelper.getHelper();
        ColumnInfo[] columnInfos = helper.getColumnInfos(this.catalog, this.tableName);

        ModelClassBuilder builder = new ModelClassBuilder(
            null, tableName, columnInfos, DatabaseType.of(connection), context.getNameConverter()
        );

        if (useLombok) {
            useLombok(builder);
        }

        if (useMybatisPlus) {
            useMybatisPlus(builder);
        }

        ClassDef classDef = builder.build(tableName);
        classDef.setClassName(pojoName);

        return classDef.toString();
    }

    private void useMybatisPlus(ModelClassBuilder builder) {
        builder.addImports("com.baomidou.mybatisplus.annotations.*");

        builder.addAnnotation(
            new AnnotationDef("TableName").setProperty("\"" + builder.getTableName() + "\""));

        builder.addAfterFieldListener(
            (columnInfo, fieldDef) -> {
                if (columnInfo.isPrimary()) {
                    fieldDef.addAnnotation("TableId")
                        .setProperty("\"" + columnInfo.getColumnName() + "\"");
                } else {
                    fieldDef.addAnnotation("TableField")
                        .setProperty("\"" + columnInfo.getColumnName() + "\"");
                }
            }
        );
    }

    private void useLombok(ModelClassBuilder builder) {
        builder.addAnnotation("Data");
        builder.addImports("lombok.Data");
        builder.setGettersEnabled(false);
        builder.setSettersEnabled(false);
    }
}

package com.hyd.dao.mate.generator;

import com.hyd.dao.database.ColumnInfo;
import com.hyd.dao.database.DatabaseType;
import com.hyd.dao.database.commandbuilder.helper.CommandBuilderHelper;
import com.hyd.dao.database.executor.ExecutionContext;
import com.hyd.dao.database.type.NameConverter;
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

        CommandBuilderHelper helper = CommandBuilderHelper.getHelper(context);
        ColumnInfo[] columnInfos = helper.getColumnInfos(this.catalog, this.tableName);

        ModelClassBuilder modelClassBuilder = new ModelClassBuilder(
            null, tableName, columnInfos, DatabaseType.of(connection), context.getNameConverter()
        );

        ClassDef classDef = modelClassBuilder.build(tableName);
        classDef.setClassName(pojoName);

        return classDef.toString();
    }
}

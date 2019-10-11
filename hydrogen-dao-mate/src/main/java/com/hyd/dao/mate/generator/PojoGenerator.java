package com.hyd.dao.mate.generator;

import java.sql.Connection;

public class PojoGenerator {

    private Connection connection;

    private String pojoName;

    private String catalog;

    private String tableName;

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

    public String generateCode() {
        return "public class " + pojoName + " {\n"
            + "\n"
            + "}";
    }
}

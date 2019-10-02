package com.hyd.dao.database.commandbuilder.helper;

/**
 * 定义不同数据库对字段定义的 meta
 *
 * @author yiding.he
 */
public enum ColumnMeta {

    Oracle("COLUMN_NAME", "COLUMN_SIZE", "NULLABLE", "DATA_TYPE", "TYPE_NAME", "REMARKS"),
    MySQL( "COLUMN_NAME", "COLUMN_SIZE", "NULLABLE", "DATA_TYPE", "TYPE_NAME", "REMARKS"),;

    /////////////////////////////////////////////////////////

    public final String columnName;

    public final String columnSize;

    public final String nullable;

    public final String dataType;

    public final String typeName;

    public final String remarks;

    ColumnMeta(String columnName, String columnSize, String nullable, String dataType, String typeName, String remarks) {
        this.columnName = columnName;
        this.columnSize = columnSize;
        this.nullable = nullable;
        this.dataType = dataType;
        this.typeName = typeName;
        this.remarks = remarks;
    }

}

package com.hyd.dao.database;

/**
 * 用来描述库表字段信息的类
 */
public class ColumnInfo {

    /**
     * 字段名
     */
    private String columnName;

    /**
     * 数据类型，参考 {@link java.sql.Types}
     */
    private int dataType;

    /**
     * 是否是主键
     */
    private boolean primary;

    /**
     * 是否自动增长
     */
    private boolean autoIncrement;

    /**
     * 序列名
     */
    private String sequenceName;

    /**
     * 注释
     */
    private String comment;

    /**
     * 最大大小
     */
    private int size;

    /**
     * 是否可以为空
     */
    private boolean nullable;

    public boolean isNullable() {
        return nullable;
    }

    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }

    public boolean isAutoIncrement() {
        return autoIncrement;
    }

    public void setAutoIncrement(boolean autoIncrement) {
        this.autoIncrement = autoIncrement;
    }

    public String getSequenceName() {
        return sequenceName;
    }

    public void setSequenceName(String sequenceName) {
        this.sequenceName = sequenceName;
    }

    /**
     * 获取字段最大长度
     *
     * @return 字段最大长度
     */
    public int getSize() {
        return size;
    }

    /**
     * 设置字段最大长度
     *
     * @param size 字段最大长度
     */
    public void setSize(int size) {
        this.size = size;
    }

    /**
     * 获取注释
     *
     * @return 注释
     */
    public String getComment() {
        return comment;
    }

    /**
     * 设置注释
     *
     * @param comment 注释
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * 获取字段是不是主键
     *
     * @return 如果字段是主键，则返回 true，否则返回 false
     */
    public boolean isPrimary() {
        return primary;
    }

    /**
     * 设置字段的主键类型
     *
     * @param primary true 表示该字段是主键。false 则表示不是。
     */
    public void setPrimary(boolean primary) {
        this.primary = primary;
    }

    /**
     * 获得字段名
     *
     * @return 字段名
     */
    public String getColumnName() {
        return columnName;
    }

    /**
     * 设置字段名
     *
     * @param columnName 字段名
     */
    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    /**
     * 获得字段数据类型
     *
     * @return 字段数据类型。具体的值参考 {@link java.sql.Types}
     */
    public int getDataType() {
        return dataType;
    }

    /**
     * 设置字段数据类型
     *
     * @param dataType 字段数据类型。具体的值参考 {@link java.sql.Types}
     */
    public void setDataType(int dataType) {
        this.dataType = dataType;
    }

    @Override
    public String toString() {
        return "ColumnInfo{" +
                "columnName='" + columnName + '\'' +
                ", dataType=" + dataType +
                ", primary=" + primary +
                ", autoIncrement=" + autoIncrement +
                ", sequenceName='" + sequenceName + '\'' +
                ", comment='" + comment + '\'' +
                ", size=" + size +
                ", nullable=" + nullable +
                '}';
    }
}

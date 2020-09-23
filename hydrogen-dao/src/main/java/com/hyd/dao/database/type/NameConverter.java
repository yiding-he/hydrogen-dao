package com.hyd.dao.database.type;

import com.hyd.dao.mate.util.Str;

/**
 * 名称转换规则。
 */
public abstract class NameConverter {

    /**
     * 不做任何转换
     */
    public static final NameConverter NONE = new NameConverter() {

        @Override
        public String field2Column(String prop) {
            return prop;
        }

        @Override
        public String column2Field(String column) {
            return column;
        }
    };

    //////////////////////////////////////////////////////////////

    /**
     * 属性名为驼峰风格（如"userName"），字段名为下划线隔开（如"user_name"）
     */
    public static final NameConverter CAMEL_UNDERSCORE = new NameConverter() {

        @Override
        public String field2Column(String prop) {
            return Str.propertyToColumn(prop);
        }

        @Override
        public String column2Field(String column) {
            return Str.columnToProperty(column);
        }
    };

    //////////////////////////////////////////////////////////////

    public static final NameConverter DEFAULT = CAMEL_UNDERSCORE;

    public abstract String field2Column(String prop);

    public abstract String column2Field(String column);
}

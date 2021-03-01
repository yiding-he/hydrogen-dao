package com.hyd.dao.database.type;

import com.hyd.dao.mate.util.Str;

/**
 * 名称转换规则。
 */
public interface NameConverter {

    NameConverter NONE = new NoneNameConverter();

    NameConverter CAMEL_UNDERSCORE = new CamelUnderscoreNameConverter();

    NameConverter DEFAULT = CAMEL_UNDERSCORE;

    //////////////////////////////////////////////////////////////

    // 这个方法暂时没用到，因为目前属性名转字段名的方法是
    // 先分析表字段，再转成属性名，再在类的属性中寻找匹配
    String field2Column(String prop);

    String column2Field(String column);

    //////////////////////////////////////////////////////////////

    /**
     * 不做任何转换
     */
    class NoneNameConverter implements NameConverter {

        @Override
        public String field2Column(String prop) {
            return prop;
        }

        @Override
        public String column2Field(String column) {
            return column;
        }
    }

    /**
     * 属性名为驼峰风格（如"userName"），字段名为下划线隔开（如"user_name"）
     */
    class CamelUnderscoreNameConverter implements NameConverter {

        @Override
        public String field2Column(String prop) {
            return Str.propertyToColumn(prop);
        }

        @Override
        public String column2Field(String column) {
            return Str.columnToProperty(column);
        }
    }
}

package com.hyd.dao.database.type;

import com.hyd.dao.util.Str;
import com.hyd.dao.util.TypeUtil;

import java.lang.reflect.Field;

public abstract class NameConverter {

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

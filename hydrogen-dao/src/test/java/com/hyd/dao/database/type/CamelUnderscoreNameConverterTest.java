package com.hyd.dao.database.type;

import org.junit.jupiter.api.Test;

import static com.hyd.dao.database.type.NameConverter.CAMEL_UNDERSCORE;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CamelUnderscoreNameConverterTest {

    @Test
    public void testColumnToField() throws Exception {
        NameConverter converter = CAMEL_UNDERSCORE;
        assertEquals("id", converter.column2Field("ID"));
        assertEquals("userName", converter.column2Field("USER_NAME"));
    }
}

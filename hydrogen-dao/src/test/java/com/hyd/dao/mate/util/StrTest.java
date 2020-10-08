package com.hyd.dao.mate.util;

import org.junit.Test;

import java.time.Year;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class StrTest {

    @Test
    public void eval() {
        Map<String, Object> emptyMap = Collections.emptyMap();
        Map<String, Object> variableMap = new HashMap<String, Object>(){{
            put("name", "HydrogenDAO");
        }};

        assertEquals("Hello", Str.eval("Hello", emptyMap));
        assertEquals("Hello, HydrogenDAO", Str.eval("Hello, {name}", variableMap));
        assertEquals("Hello, ", Str.eval("Hello, {}", variableMap));
        assertEquals("Hello, \\{HydrogenDAO", Str.eval("Hello, \\{{name}", variableMap));
        assertEquals("Hello, \\{}HydrogenDAO", Str.eval("Hello, \\{}{name}", variableMap));
        assertEquals("Hello, \\{123}HydrogenDAO", Str.eval("Hello, \\{123}{name}", variableMap));
        assertEquals("Hello, \\{\\}HydrogenDAO", Str.eval("Hello, \\{\\}{name}", variableMap));
        assertEquals("Hello, \\{123\\}HydrogenDAO", Str.eval("Hello, \\{123\\}{name}", variableMap));

        variableMap.put("year", Year.now());
        assertEquals("Hello, HydrogenDAO, now is 2020.",
            Str.eval("Hello, {name}, now is {year}.", variableMap));
    }
}

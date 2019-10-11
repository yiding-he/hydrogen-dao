package com.hyd.dao.mate.generator.code;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * (description)
 * created at 2018/4/10
 *
 * @author yidin
 */
public class AnnotationDef implements Code {

    public String name;

    public Map<String, String> properties = new HashMap<>();

    public AnnotationDef() {
    }

    public AnnotationDef(String name) {
        this.name = name;
    }

    public AnnotationDef setProperty(String key, String value) {
        this.properties.put(key, value);
        return this;
    }

    public AnnotationDef setProperty(String value) {
        this.properties.put("value", value);
        return this;
    }

    @Override
    public CodeBlock toCodeBlock() {
        String propString = "";

        if (!properties.isEmpty()) {
            propString = "(";

            if (properties.size() == 1 && properties.containsKey("value")) {
                propString += properties.values().stream().findFirst().orElse("");
            } else {
                propString += properties.entrySet().stream()
                        .map(e -> e.getKey() + "=" + e.getValue())
                        .collect(Collectors.joining(","));
            }

            propString += ")";
        }

        return new CodeBlock("@" + name + propString);
    }
}


package com.hyd.dao.src;

import java.util.ArrayList;
import java.util.List;

/**
 * (description)
 * created at 2018/4/10
 *
 * @author yidin
 */
public class ClassDef {

    public String className;

    public List<FieldDef> fields = new ArrayList<>();

    public List<MethodDef> methods = new ArrayList<>();

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();

        s.append("public class ").append(className).append(" {\n");

        for (FieldDef field : fields) {
            s.append(field.toString());
        }

        for (MethodDef method : methods) {
            s.append(method.toString());
        }

        s.append("}");

        return s.toString();
    }

    private boolean containsField(FieldDef fieldDef) {
        return fields.stream().anyMatch(f -> f.name.equals(fieldDef.name));
    }

    public void addFieldIfNotExists(FieldDef fieldDef) {
        if (containsField(fieldDef)) {
            return;
        }

        this.fields.add(fieldDef);
    }
}

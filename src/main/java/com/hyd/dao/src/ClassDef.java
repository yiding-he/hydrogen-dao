package com.hyd.dao.src;

import java.util.ArrayList;
import java.util.List;

/**
 * (description)
 * created at 2018/4/10
 *
 * @author yidin
 */
public class ClassDef implements Code {

    public String className;

    public List<FieldDef> fields = new ArrayList<>();

    public List<MethodDef> methods = new ArrayList<>();

    @Override
    public CodeBlock toCodeBlock() {
        CodeBlock codeBlock = new CodeBlock();
        codeBlock.addLine("public", "class", className, "{");
        codeBlock.addLine();

        for (FieldDef field : fields) {
            codeBlock.addCode(field, true);
            codeBlock.addLine();
        }

        for (MethodDef method : methods) {
            codeBlock.addCode(method, true);
        }

        codeBlock.addLine("}");
        return codeBlock;
    }

    @Override
    public String toString() {
        return toCodeBlock().toCode();
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

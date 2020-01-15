package com.hyd.dao.mate.generator.code;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

/**
 * 类的定义
 *
 * @author yidin
 */
public class ClassDef implements Code {

    public PackageDef packageDef;

    public ImportDef imports;

    public List<AnnotationDef> annotations = new ArrayList<>();

    public String className;

    public List<FieldDef> fields = new ArrayList<>();

    public List<MethodDef> methods = new ArrayList<>();

    @Override
    public CodeBlock toCodeBlock() {
        CodeBlock codeBlock = new CodeBlock();

        if (packageDef != null) {
            codeBlock.addCode(packageDef, false);
            codeBlock.addLine();
        }

        if (imports != null) {
            codeBlock.addCode(imports, false);
            codeBlock.addLine();
        }

        for (AnnotationDef annotation : annotations) {
            codeBlock.addCode(annotation, false);
        }

        codeBlock.addLine("public", "class", className, "{");
        codeBlock.addLine();

        for (FieldDef field : fields) {
            codeBlock.addCode(field, true);
            codeBlock.addLine();
        }

        for (MethodDef method : methods) {
            codeBlock.addCode(method, true);
            codeBlock.addLine();
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

    public void forEachField(Consumer<FieldDef> fieldDefConsumer) {
        this.fields.forEach(fieldDefConsumer);
    }

    public ImportDef getImports() {
        return imports;
    }

    public void setImports(ImportDef imports) {
        this.imports = imports;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public void addMethod(MethodDef methodDef) {
        this.methods.add(methodDef);
    }

    public void addAnnotation(AnnotationDef annotationDef) {
        this.annotations.add(annotationDef);
    }

    public void addAnnotations(Collection<AnnotationDef> defs) {
        this.annotations.addAll(defs);
    }
}

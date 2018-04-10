package com.hyd.dao.src;

/**
 * (description)
 * created at 2018/4/10
 *
 * @author yidin
 */
public class FieldDef {

    public AccessType access;

    public AnnotationDef annotation;

    public String name;

    public String type;

    public String value;

    @Override
    public String toString() {
        CodeBlock codeBlock = new CodeBlock(0);
        
        String s = "\n";
        if (annotation != null) {
            s += annotation.toString();
        }
        if (access != null) {
            s += access.name().toLowerCase() + " ";
        }
        s += type + " " + name;
        if (value != null) {
            s += " = " + value;
        }
        return s + ";\n\n";
    }
}

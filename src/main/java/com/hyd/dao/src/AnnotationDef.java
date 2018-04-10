package com.hyd.dao.src;

/**
 * (description)
 * created at 2018/4/10
 *
 * @author yidin
 */
public class AnnotationDef implements Code {

    public String name;

    public AnnotationDef() {
    }

    public AnnotationDef(String name) {
        this.name = name;
    }

    @Override
    public CodeBlock toCodeBlock() {
        return new CodeBlock("@" + name);
    }
}


package com.hyd.dao.src;

/**
 * (description)
 * created at 2018/4/10
 *
 * @author yidin
 */
public class AnnotationDef {

    public String name;

    public AnnotationDef() {
    }

    public AnnotationDef(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        CodeBlock codeBlock = new CodeBlock(0);
        codeBlock.addLine("@");
    }
}

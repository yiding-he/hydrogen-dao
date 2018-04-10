package com.hyd.dao.src;

/**
 * (description)
 * created at 2018/4/10
 *
 * @author yidin
 */
public class FieldDef implements Code {

    public AccessType access;

    public AnnotationDef annotation;

    public String name;

    public String type;

    public String value;

    @Override
    public CodeBlock toCodeBlock() {
        CodeBlock codeBlock = new CodeBlock();
        codeBlock.addCode(this.annotation, false);
        codeBlock.addLine(
                (access == null ? "" : (access.name().toLowerCase() + " ")) +
                        type + " " + name +
                        (value == null ? "" : (" = " + value)) + ";"
        );
        return codeBlock;
    }
}

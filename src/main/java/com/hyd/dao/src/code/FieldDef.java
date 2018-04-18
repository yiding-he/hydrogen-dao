package com.hyd.dao.src.code;

import org.apache.commons.lang3.StringUtils;

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

    public MethodDef toGetterMethod() {
        MethodDef method = new MethodDef();
        method.name = (type.equalsIgnoreCase("boolean") ? "is" : "get") + StringUtils.capitalize(name);
        method.access = AccessType.Public;
        method.type = type;
        method.body = new CodeBlock("return this." + name + ";");
        return method;
    }

    public MethodDef toSetterMethod() {
        MethodDef method = new MethodDef();
        method.name = "set" + StringUtils.capitalize(name);
        method.access = AccessType.Public;
        method.args.add(new MethodArg(type, name));
        method.body = new CodeBlock("this." + name + " = " + name + ";");
        return method;
    }
}

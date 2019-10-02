package com.hyd.dao.src.code;


import com.hyd.dao.util.Str;
import java.util.ArrayList;
import java.util.List;

/**
 * (description)
 * created at 2018/4/10
 *
 * @author yidin
 */
public class FieldDef implements Code {

    public AccessType access;

    public List<AnnotationDef> annotations = new ArrayList<>();

    public String name;

    public String type;

    public String value;

    @Override
    public CodeBlock toCodeBlock() {
        CodeBlock codeBlock = new CodeBlock();

        for (AnnotationDef annotation : annotations) {
            codeBlock.addCode(annotation, false);
        }

        codeBlock.addLine(
                (access == null ? "" : (access.name().toLowerCase() + " ")) +
                        type + " " + name +
                        (value == null ? "" : (" = " + value)) + ";"
        );
        return codeBlock;
    }

    public MethodDef toGetterMethod() {
        MethodDef method = new MethodDef();
        method.name = (type.equalsIgnoreCase("boolean") ? "is" : "get") + Str.capitalize(name);
        method.access = AccessType.Public;
        method.type = type;
        method.body = new CodeBlock("return this." + name + ";");
        return method;
    }

    public MethodDef toSetterMethod() {
        MethodDef method = new MethodDef();
        method.name = "set" + Str.capitalize(name);
        method.access = AccessType.Public;
        method.args.add(new MethodArg(type, name));
        method.body = new CodeBlock("this." + name + " = " + name + ";");
        return method;
    }

    public AnnotationDef addAnnotation(String name) {
        AnnotationDef a = new AnnotationDef(name);
        this.annotations.add(a);
        return a;
    }
}

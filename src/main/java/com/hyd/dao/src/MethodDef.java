package com.hyd.dao.src;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * (description)
 * created at 2018/4/10
 *
 * @author yidin
 */
public class MethodDef implements Code {

    public AccessType access;

    public String name;

    public String type;

    public CodeBlock body;

    public List<MethodArg> args = new ArrayList<>();

    @Override
    public CodeBlock toCodeBlock() {
        CodeBlock codeBlock = new CodeBlock();
        codeBlock.addLine(
                access == null? "": access.name().toLowerCase(),
                type == null? "void": type,
                name + "(" + toArgs() + ")",
                "{"
        );
        codeBlock.addCodeBlock(body, true);
        codeBlock.addLine("}");
        return codeBlock;
    }

    private String toArgs() {
        return args.stream()
                .map(arg -> arg.type + " " + arg.name)
                .collect(Collectors.joining(", "));
    }
}

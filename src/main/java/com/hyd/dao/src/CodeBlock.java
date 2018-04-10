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
public class CodeBlock {

    private int indent;

    private List<String> lines = new ArrayList<>();

    public CodeBlock(int indent) {
        this.indent = indent;
    }

    public void addLine(String line) {
        this.lines.add(line);
    }

    public void addCodeBlock(CodeBlock codeBlock, int indent) {
        this.lines.addAll(codeBlock.toLines());
    }
    
    public List<String> toLines() {
        return lines.stream()
                .map(l -> String.format("%" + indent + "s", " ") + l)
                .collect(Collectors.toList());
    }

    public String toCode() {
        return String.join("\n", toLines());
    }
}

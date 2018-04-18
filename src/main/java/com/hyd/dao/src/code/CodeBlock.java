package com.hyd.dao.src.code;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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

    public CodeBlock(String... lines) {
        this.lines.addAll(Arrays.asList(lines));
        this.lines.removeAll(Collections.singleton(null));
    }

    public void setIndent(int indent) {
        this.indent = indent;
    }

    public void addLine(String... tokens) {
        if (tokens.length > 0) {
            this.lines.add(String.join(" ", tokens));
        } else {
            this.lines.add("");
        }
    }

    public void addLines(List<String> lines) {
        for (String line : lines) {
            addLine(line);
        }
    }

    public void addCode(Code code, boolean indent) {
        if (code != null) {
            addCodeBlock(code.toCodeBlock(), indent);
        }
    }

    public void addCodeBlock(CodeBlock codeBlock, boolean indent) {
        if (codeBlock != null) {
            codeBlock.indent = indent ? 1 : 0;
            this.lines.addAll(codeBlock.toLines());
        }
    }

    public List<String> toLines() {
        return lines.stream()
                .map(l -> (indent > 0 ? "    " : "") + l)
                .collect(Collectors.toList());
    }

    public String toCode() {
        return String.join("\n", toLines());
    }
}

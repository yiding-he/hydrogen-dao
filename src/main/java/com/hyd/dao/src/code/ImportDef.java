package com.hyd.dao.src.code;

import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * (description)
 * created at 2018/4/18
 *
 * @author yidin
 */
public class ImportDef implements Code {

    private String[] packages;

    public ImportDef(String... packages) {
        this.packages = packages;
    }

    @Override
    public CodeBlock toCodeBlock() {
        CodeBlock codeBlock = new CodeBlock();
        codeBlock.addLines(Stream.of(packages)
                .map(p -> "import " + p + ";")
                .collect(Collectors.toList()));
        return codeBlock;
    }
}

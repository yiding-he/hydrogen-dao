package com.hyd.dao.mate.generator.code;

import java.util.*;
import java.util.stream.Collectors;

/**
 * (description)
 * created at 2018/4/18
 *
 * @author yidin
 */
public class ImportDef implements Code {

    private Collection<String> imports;

    public ImportDef(String... imports) {
        this.imports = new ArrayList<>(Arrays.asList(imports));
    }

    public ImportDef(Collection<String> imports) {
        this.imports = new ArrayList<>(imports);
    }

    public ImportDef add(String fullClassName) {
        this.imports.add(fullClassName);
        return this;
    }

    public ImportDef remove(String fullClassName) {
        this.imports.remove(fullClassName);
        return this;
    }

    public ImportDef addAll(Collection<String> packages) {
        this.imports.addAll(packages);
        return this;
    }

    @Override
    public CodeBlock toCodeBlock() {
        CodeBlock codeBlock = new CodeBlock();
        codeBlock.addLines(imports.stream()
                .map(p -> "import " + p + ";")
                .collect(Collectors.toList()));
        return codeBlock;
    }
}

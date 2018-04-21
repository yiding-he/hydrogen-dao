package com.hyd.dao.src.code;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * (description)
 * created at 2018/4/18
 *
 * @author yidin
 */
public class ImportDef implements Code {

    private List<String> packages;

    public ImportDef(String... packages) {
        this.packages = new ArrayList<>(Arrays.asList(packages));
    }

    public void addPackage(String pack) {
        this.packages.add(pack);
    }

    @Override
    public CodeBlock toCodeBlock() {
        CodeBlock codeBlock = new CodeBlock();
        codeBlock.addLines(packages.stream()
                .map(p -> "import " + p + ";")
                .collect(Collectors.toList()));
        return codeBlock;
    }
}

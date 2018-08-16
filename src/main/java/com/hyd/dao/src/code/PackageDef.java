package com.hyd.dao.src.code;

/**
 * (description)
 * created at 2018/4/26
 *
 * @author yidin
 */
public class PackageDef implements Code {

    public String packageName;

    public PackageDef(String packageName) {
        this.packageName = packageName;
    }

    @Override
    public CodeBlock toCodeBlock() {
        return new CodeBlock("package " + packageName + ";");
    }
}

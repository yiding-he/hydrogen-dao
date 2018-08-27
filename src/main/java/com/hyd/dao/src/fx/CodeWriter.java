package com.hyd.dao.src.fx;

import com.hyd.dao.src.code.ClassDef;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.hyd.dao.util.Str.defaultIfEmpty;

/**
 * 写入 Java 代码
 *
 * @author yidin
 */
public class CodeWriter {

    public static final String DEFAULT_CODE_ROOT = "./src/main/java";

    public static final String DEFAULT_TEST_ROOT = "./src/test/java";

    public static final String CHARSET_NAME = "UTF-8";

    public static void writeClass(ClassDef classDef, String codeRoot) throws IOException {
        writeClass0(classDef, defaultIfEmpty(codeRoot, DEFAULT_CODE_ROOT));
    }

    public static void writeUnitTestClass(ClassDef unitClassDef, String testRoot) throws IOException {
        writeClass0(unitClassDef, defaultIfEmpty(testRoot, DEFAULT_TEST_ROOT));
    }

    private static void writeClass0(ClassDef classDef, String root) throws IOException {
        if (classDef == null || classDef.packageDef == null) {
            return;
        }

        String modelPackage = classDef.packageDef.packageName;
        Path packageDir = Paths.get(root).resolve(modelPackage.replace(".", "/"));

        if (!Files.exists(packageDir)) {
            Files.createDirectories(packageDir);
        }

        Path javaFile = packageDir.resolve(classDef.className + ".java");
        Files.write(javaFile, classDef.toString().getBytes(CHARSET_NAME));
    }
}

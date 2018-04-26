package com.hyd.dao.src.fx;

import com.hyd.dao.src.code.ClassDef;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * (description)
 * created at 2018/4/26
 *
 * @author yidin
 */
public class CodeWriter {

    public static void writeClass(ClassDef classDef) throws IOException {

        if (classDef == null || classDef.packageDef == null) {
            return;
        }

        String modelPackage = classDef.packageDef.packageName;
        Path packageDir = Paths.get("./src/main/java").resolve(modelPackage.replace(".", "/"));

        if (!Files.exists(packageDir)) {
            Files.createDirectories(packageDir);
        }

        Path javaFile = packageDir.resolve(classDef.className + ".java");
        Files.write(javaFile, classDef.toString().getBytes("UTF-8"));
    }
}

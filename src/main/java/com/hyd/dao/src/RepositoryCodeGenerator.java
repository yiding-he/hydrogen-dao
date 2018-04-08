package com.hyd.dao.src;

import com.hyd.dao.DataSources;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

/**
 * (description)
 * created at 2018/4/8
 *
 * @author yidin
 */
public class RepositoryCodeGenerator {

    private DataSources dataSources;

    public RepositoryCodeGenerator(DataSources dataSources) {
        this.dataSources = dataSources;
    }

    public ForDataSourceContext forDataSource(String dataSourceName) {
        return new ForDataSourceContext(dataSourceName);
    }

    //////////////////////////////////////////////////////////////

    public class ForDataSourceContext {

        private String dsName;

        ForDataSourceContext(String dsName) {
            this.dsName = dsName;
        }

        public FromTableContext fromTable(String tableName) {
            return new FromTableContext(tableName);
        }

        //////////////////////////////////////////////////////////////

        public class FromTableContext {

            private String tableName;

            public FromTableContext(String tableName) {
                this.tableName = tableName;
            }

            //////////////////////////////////////////////////////////////

            public void toFile(String filePath, String charset) throws IOException {
                toFile(new File(filePath), charset);
            }

            public void toFile(File file, String charset) throws IOException {
                Path path = file.toPath();

                if (Files.exists(path)) {
                    if (Files.isDirectory(path)) {
                        return;
                    }
                } else {
                    Path parent = path.getParent();
                    if (!Files.exists(parent)) {
                        Files.createDirectory(parent);
                    }

                    Files.createFile(path);
                }

                Files.write(path, generateContent(dsName, tableName, file.getName()), Charset.forName(charset));
            }
        }
    }

    private List<String> generateContent(String dataSourceName, String tableName, String fileName) {
        String className = StringUtils.substringBeforeLast(fileName, ".");
        return Arrays.asList(
                "public class " + className + " {",
                "}"
        );
    }
}

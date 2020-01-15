package com.hyd.dao.junit;

import com.hyd.dao.*;
import com.hyd.dao.mate.util.CSVReader;
import com.hyd.dao.mate.util.ScriptExecutor;
import java.io.File;
import java.util.*;
import java.util.function.Supplier;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * 用于单元测试的 Rule
 *
 * @author yidin
 */
public class HydrogenDAORule implements TestRule {

    private static final String SCRIPT_FOLDER = "junit-rule-scripts";

    private final Supplier<DAO> daoSupplier;

    private final String scriptFolder;

    private final Map<String, List<Row>> prepareData = new HashMap<>();

    public HydrogenDAORule(Supplier<DAO> daoSupplier) {
        this.daoSupplier = daoSupplier;
        this.scriptFolder = SCRIPT_FOLDER;
        init();
    }

    public HydrogenDAORule(Supplier<DAO> daoSupplier, String scriptFolder) {
        this.daoSupplier = daoSupplier;
        this.scriptFolder = scriptFolder;
        init();
    }

    private void init() {

        // 搜索文件列表
        List<File> csvFiles = scanCsvFiles();

        // 将文件内容插入到数据库
        for (File csvFile : csvFiles) {
            String fileName = csvFile.getName();
            String tableName = fileName.substring(0, fileName.length() - 4);
            List<Row> rows = CSVReader.read(csvFile, "UTF-8");
            prepareData.put(tableName, rows);
        }
    }

    /**
     * 检查 {@link #scriptFolder} 目录下是否有 csv 文件
     *
     * @return csv 文件列表
     */
    private List<File> scanCsvFiles() {
        String classPath = System.getProperty("java.class.path", ".");
        String[] classPathElements = classPath.split(System.getProperty("path.separator"));
        List<File> csvFiles = new Page<>();

        for (String pathElement : classPathElements) {
            File file = new File(pathElement);
            if (file.exists() && file.isDirectory()) {
                File csvFolder = new File(file, scriptFolder);
                if (csvFolder.exists() && csvFolder.isDirectory()) {
                    File[] files = csvFolder.listFiles(f -> f.getName().toLowerCase().endsWith(".csv"));
                    if (files != null) {
                        csvFiles.addAll(Arrays.asList(files));
                    }
                }
            }
        }

        return csvFiles;
    }

    private void insertData(DAO dao) {
        prepareData.forEach((tableName, rows) -> dao.insert(rows, tableName));
    }

    private void before() {
        DAO dao = daoSupplier.get();

        ScriptExecutor.execute("classpath:/" + scriptFolder + "/tables.sql", dao);
        insertData(dao);
        ScriptExecutor.execute("classpath:/" + scriptFolder + "/before.sql", dao);
    }

    @Override
    public Statement apply(Statement statement, Description description) {
        return new Statement() {

            @Override
            public void evaluate() throws Throwable {
                before();
                statement.evaluate();
                after();
            }
        };
    }

    private void after() {
        DAO dao = daoSupplier.get();
        ScriptExecutor.execute("classpath:/" + scriptFolder + "/after.sql", dao);
    }
}

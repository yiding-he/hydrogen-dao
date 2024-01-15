package com.hyd.dao.junit;

import com.hyd.dao.DAO;
import com.hyd.dao.Row;
import com.hyd.dao.mate.util.CSVReader;
import com.hyd.dao.mate.util.ScriptExecutor;

import java.io.File;
import java.util.*;
import java.util.function.Supplier;

/**
 * 用于单元测试的 Rule
 *
 * @author yidin
 */
public class HydrogenDAORule {

    private static final String SCRIPT_FOLDER = "scripts";

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
        var csvFiles = scanCsvFiles();

        // 将文件内容插入到数据库
        for (var csvFile : csvFiles) {
            var fileName = csvFile.getName();
            var tableName = fileName.substring(0, fileName.length() - 4);
            var rows = CSVReader.read(csvFile, "UTF-8");
            prepareData.put(tableName, rows);
        }
    }

    /**
     * 检查 {@link #scriptFolder} 目录下是否有 csv 文件
     *
     * @return csv 文件列表
     */
    private List<File> scanCsvFiles() {
        var classPath = System.getProperty("java.class.path", ".");
        var classPathElements = classPath.split(File.pathSeparator);
        List<File> csvFiles = new ArrayList<>();

        for (var pathElement : classPathElements) {
            var file = new File(pathElement);
            if (file.exists() && file.isDirectory()) {
                var csvFolder = new File(file, scriptFolder);
                if (csvFolder.exists() && csvFolder.isDirectory()) {
                    var files = csvFolder.listFiles(f -> f.getName().toLowerCase().endsWith(".csv"));
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

    public void before() {
        var dao = daoSupplier.get();

        ScriptExecutor.execute("classpath:/" + scriptFolder + "/tables.sql", dao);
        insertData(dao);
        ScriptExecutor.execute("classpath:/" + scriptFolder + "/before.sql", dao);
    }

    public void after() {
        var dao = daoSupplier.get();
        ScriptExecutor.execute("classpath:/" + scriptFolder + "/after.sql", dao);
    }
}

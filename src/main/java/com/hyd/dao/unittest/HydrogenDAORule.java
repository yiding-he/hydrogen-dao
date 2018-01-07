package com.hyd.dao.unittest;

import com.hyd.dao.DAO;
import com.hyd.dao.Page;
import com.hyd.dao.Row;
import com.hyd.dao.util.CSVReader;
import com.hyd.dao.util.ScriptExecutor;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @author yidin
 */
public class HydrogenDAORule implements TestRule {

    private static final String SCRIPT_FOLDER = "hydrogen-scripts";

    private final Supplier<DAO> daoSupplier;

    private final String scriptFolder;

    private Map<String, List<Row>> prepareData = new HashMap<>();

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
        List<File> csvFiles = scanCsvFiles();

        for (File csvFile : csvFiles) {
            String fileName = csvFile.getName();
            String tableName = fileName.substring(0, fileName.length() - 4);
            List<Row> rows = CSVReader.read(csvFile, "UTF-8");
            prepareData.put(tableName, rows);
        }
    }

    private List<File> scanCsvFiles() {
        String classPath = System.getProperty("java.class.path", ".");
        String[] classPathElements = classPath.split(System.getProperty("path.separator"));
        List<File> csvFiles =new Page<>();

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

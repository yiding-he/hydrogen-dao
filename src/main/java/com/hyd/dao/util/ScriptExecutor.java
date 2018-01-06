package com.hyd.dao.util;

import com.hyd.dao.DAO;
import com.hyd.dao.DAOException;
import com.hyd.dao.log.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 用来执行 SQL 文件的类。
 * <p>
 * 要求：
 * 1、只支持创建表、视图、索引等单条语句，不支持创建存储过程；
 * 2、SQL 语句可以多行，但必须以分号结尾；
 * 3、注释占整行，必须以 // 或 -- 开头，不支持 SQL 行尾加上注释。
 *
 * @author yidin
 */
public class ScriptExecutor {

    private static final Logger LOG = Logger.getLogger(ScriptExecutor.class);

    public static void execute(String filePath, DAO dao, Charset charset) throws IOException {
        execute(new File(filePath), dao, charset);
    }

    public static void execute(File file, DAO dao, Charset charset) throws IOException {
        if (!file.exists() || !file.isFile()) {
            throw new DAOException("Invalid file '" + file.getAbsolutePath() + "'");
        } else {
            InputStream inputStream = null;
            try {
                inputStream = new FileInputStream(file);
                execute(inputStream, dao, charset);
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
            }
        }
    }

    public static void execute(String resourcePath, DAO dao) {
        execute(ScriptExecutor.class.getResourceAsStream(resourcePath), dao, Charset.forName("UTF-8"));
    }

    public static void execute(String resourcePath, DAO dao, String charset) {
        execute(ScriptExecutor.class.getResourceAsStream(resourcePath), dao, Charset.forName(charset));
    }

    public static void execute(InputStream is, DAO dao, Charset charset) {

        if (is == null) {
            throw new DAOException("Invalid input stream");
        }

        Scanner scanner = new Scanner(is, charset.name());
        String line;
        StringBuilder statement = new StringBuilder();
        AtomicInteger counter = new AtomicInteger();

        while (scanner.hasNextLine()) {
            line = scanner.nextLine().trim();

            if (line.startsWith("//") || line.startsWith("--")) {
                continue;
            }

            statement.append(line);

            if (line.endsWith(";")) {
                executeStatement(dao, statement.toString(), counter);
                statement = new StringBuilder();
            }
        }

        String finalStatement = statement.toString();
        if (finalStatement.trim().length() > 0) {
            executeStatement(dao, finalStatement, counter);
        }
    }

    private static void executeStatement(DAO dao, String statement, AtomicInteger counter) {
        dao.execute(statement);
        LOG.info(counter.incrementAndGet() + " statements executed.");
    }
}

package com.hyd.dao.mate.util;

import com.hyd.dao.DAO;
import com.hyd.dao.DAOException;
import com.hyd.dao.log.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
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

    private static final String CLASSPATH = "classpath:";

    public static void execute(File file, DAO dao, Charset charset) {
        if (!file.exists() || !file.isFile()) {
            throw new DAOException("Invalid file '" + file.getAbsolutePath() + "'");
        } else {
            try {
                InputStream inputStream = new FileInputStream(file);
                execute(inputStream, dao, charset);
            } catch (FileNotFoundException e) {
                throw new DAOException(e);
            }
        }
    }

    public static void execute(String resourcePath, DAO dao) {
        execute(resourcePath, dao, StandardCharsets.UTF_8);
    }

    public static void execute(String resourcePath, DAO dao, String charset) {
        execute(resourcePath, dao, Charset.forName(charset));
    }

    public static void execute(String path, DAO dao, Charset charset) {

        LOG.info(() -> "Executing script '" + path + "'...");
        InputStream inputStream;

        if (path.startsWith(CLASSPATH)) {
            inputStream = ScriptExecutor.class
                    .getResourceAsStream(path.substring(CLASSPATH.length()));
        } else {
            try {
                inputStream = new FileInputStream(path);
            } catch (FileNotFoundException e) {
                throw new DAOException(e);
            }
        }

        execute(inputStream, dao, charset);
    }

    public static void execute(InputStream is, DAO dao, Charset charset) {

        if (is == null) {
            throw new DAOException("Invalid input stream");
        }

        AtomicInteger counter = new AtomicInteger();

        try {
            executeStatements(is, dao, charset, counter);
            LOG.info(() -> counter.get() + " statements executed successfully.");
        } catch (RuntimeException e) {
            LOG.error(() -> counter.get() + " statements executed before exception.");
            throw e;
        }
    }

    private static void executeStatements(
            InputStream is, DAO dao, Charset charset, AtomicInteger counter) {

        String line;
        StringBuilder statement = new StringBuilder();
        try (Scanner scanner = new Scanner(is, charset.name())) {
            while (scanner.hasNextLine()) {
                line = scanner.nextLine().trim();

                // 整行为注释内容，略过
                if (line.startsWith("//") || line.startsWith("--")) {
                    continue;
                }

                // 对于可能出现的行尾注释，如果 "--" 是出现在单引号内部，则不视为注释
                line = fixInlineComments(line).trim();

                statement.append(" ").append(line);

                if (line.endsWith(";")) {
                    executeStatement(dao, statement.toString());
                    counter.incrementAndGet();
                    statement = new StringBuilder();
                }
            }
        }

        String finalStatement = statement.toString();
        if (finalStatement.trim().length() > 0) {
            executeStatement(dao, finalStatement);
            counter.incrementAndGet();
        }
    }

    private static String fixInlineComments(String line) {
        int index = line.indexOf("--");
        while (index != -1) {
            if (isComment(line, index)) {
                return line.substring(0, index);
            }
            index = line.indexOf("--", index + 2);
        }
        return line;
    }

    private static boolean isComment(String line, int index) {
        int count = Str.countMatches(line.substring(0, index), "'");
        return count % 2 == 0;
    }

    private static void executeStatement(DAO dao, String statement) {
        dao.execute(statement);
    }
}

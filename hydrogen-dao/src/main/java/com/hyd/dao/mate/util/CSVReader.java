package com.hyd.dao.mate.util;

import com.hyd.dao.DAOException;
import com.hyd.dao.Row;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * 从 csv 文件中读取内容到 Row 列表
 */
public class CSVReader {

    /**
     * 从 csv 文件读取内容到 Row 列表
     *
     * @param path    资源路径
     * @param charset 编码
     *
     * @return 读取结果
     *
     * @throws DAOException 如果读取失败
     */
    public static List<Row> read(String path, String charset) throws DAOException {

        InputStream inputStream;
        if (path.startsWith("classpath:")) {
            inputStream = CSVReader.class.getResourceAsStream(path.substring("classpath:".length()));
        } else {
            try {
                inputStream = new FileInputStream(path);
            } catch (FileNotFoundException e) {
                throw new DAOException(e);
            }
        }

        return read(inputStream, charset);
    }

    public static List<Row> read(File file, String charset) throws DAOException {
        try {
            return read(new FileInputStream(file), Charset.forName(charset));
        } catch (FileNotFoundException e) {
            throw new DAOException(e);
        }
    }

    public static List<Row> read(InputStream inputStream, String charset) throws DAOException {
        return read(inputStream, Charset.forName(charset));
    }

    public static List<Row> read(InputStream inputStream, Charset charset) throws DAOException {

        if (inputStream == null) {
            throw new NullPointerException("input stream is null");
        }

        List<String> lines = new ArrayList<>();
        String line;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, charset))) {
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            throw new DAOException(e);
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                // nothing to do
            }
        }

        return convertLines(lines);
    }

    private static List<Row> convertLines(List<String> lines) {
        String[] columns = lines.get(0).split(",");
        List<Row> rows = new ArrayList<Row>();

        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            String[] values = line.split(",");
            Row row = new Row();

            for (int j = 0; j < columns.length; j++) {
                String column = columns[j];
                String value = values[j];
                row.put(column, value);
            }

            rows.add(row);
        }

        return rows;
    }
}

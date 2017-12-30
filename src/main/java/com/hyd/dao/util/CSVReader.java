package com.hyd.dao.util;

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
     * @throws IOException 如果读取失败
     */
    public static List<Row> read(String path, String charset) throws IOException {
        return read(new FileInputStream(path), charset);
    }

    public static List<Row> read(InputStream inputStream, String charset) throws IOException {
        return read(inputStream, Charset.forName(charset));
    }

    public static List<Row> read(InputStream inputStream, Charset charset) throws IOException {

        if (inputStream == null) {
            throw new NullPointerException("input stream is null");
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, charset));

        List<String> lines = new ArrayList<String>();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } finally {
            reader.close();
            inputStream.close();
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

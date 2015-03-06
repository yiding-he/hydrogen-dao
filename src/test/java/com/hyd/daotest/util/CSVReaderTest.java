package com.hyd.daotest.util;

import com.hyd.dao.Row;
import com.hyd.dao.util.CSVReader;
import org.junit.Test;

import java.util.List;

public class CSVReaderTest {

    @Test
    public void testRead() throws Exception {
        List<Row> rows = CSVReader.read("/1.csv", "GBK");

        for (Row row : rows) {
            System.out.println(row);
        }
    }
}
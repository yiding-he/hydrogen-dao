package com.hyd.dao.database.type;

import java.io.IOException;
import java.sql.Clob;
import java.sql.SQLException;

/**
 * 从 Clob 读取信息
 */
public class ClobUtil {

    public static String read(Clob clob) throws IOException, SQLException {
        var reader = clob.getCharacterStream();
        var result = new char[0];
        var buf = new char[4096];
        int size;
        while ((size = reader.read(buf)) != -1) {
            var new_result = new char[result.length + size];
            System.arraycopy(result, 0, new_result, 0, result.length);
            System.arraycopy(buf, 0, new_result, result.length, size);
            result = new_result;
        }
        return new String(result);
    }

    public static void write(Clob clob, String text) throws SQLException, IOException {
        var out = clob.setCharacterStream(1);
        out.write(text);
        out.flush();
        out.close();
    }
}

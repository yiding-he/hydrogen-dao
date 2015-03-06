package com.hyd.dao.database.type;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.SQLException;

/**
 * 从 Blob 读取信息
 */
public class BlobReader {

    /**
     * 从 BLOB 对象中读取字符串
     *
     * @param blob    BLOB 对象
     * @param charset 编码
     *
     * @return 读取到的字符串
     *
     * @throws SQLException 如果数据库访问 BLOB 失败
     * @throws IOException  如果从流中读取内容失败
     */
    public static String readString(Blob blob, String charset) throws SQLException, IOException {
        byte[] result = readBytes(blob);
        return new String(result);
    }

    /**
     * 从 BLOB 对象中读取字节数组
     *
     * @param blob BLOB 对象
     *
     * @return 字节数组
     *
     * @throws SQLException 如果数据库访问 BLOB 失败
     * @throws IOException  如果从流中读取内容失败
     */
    public static byte[] readBytes(Blob blob) throws SQLException, IOException {
        InputStream stream = blob.getBinaryStream();
        byte[] result = new byte[0];
        byte[] buf = new byte[4096];
        int size;
        while ((size = stream.read(buf)) != -1) {
            byte[] new_result = new byte[result.length + size];
            System.arraycopy(result, 0, new_result, 0, result.length);
            System.arraycopy(buf, 0, new_result, result.length, size);
            result = new_result;
        }
        return result;
    }
}

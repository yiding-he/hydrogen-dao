package com.hyd.dao.mate.util;

import com.hyd.dao.Page;
import com.hyd.dao.Row;
import com.hyd.dao.database.type.NameConverter;
import com.hyd.dao.database.type.TypeConverter;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 用于处理 ResultSet 的辅助类
 */
@SuppressWarnings({"unchecked"})
public class ResultSetUtil {

    public static final String PAGINATION_WRAPPER_COLUMN_NAME = "pagination_wrapper_column_name";

    /**
     * 查询 ResultSet 中有哪些字段
     *
     * @param rs ResultSet 对象
     *
     * @return rs 包含的字段名
     *
     * @throws SQLException 如果获取失败
     */
    public static List<String> getColumnNames(ResultSet rs) throws SQLException {
        int column_count = rs.getMetaData().getColumnCount();
        List<String> result = new ArrayList<String>();
        for (int i = 0; i < column_count; i++) {
            result.add(rs.getMetaData().getColumnName(i + 1));
        }
        return result;
    }

    /**
     * 将查询结果包装为 HashMap
     *
     * @param rs 已经移至当前行的查询结果
     *
     * @return 包装好的查询结果
     *
     * @throws java.sql.SQLException 如果查询失败
     * @throws java.io.IOException   如果获取值失败
     */
    public static Row readRow(ResultSet rs) throws SQLException, IOException {
        Row row = new Row();
        ResultSetMetaData meta = rs.getMetaData();
        for (int i = 0; i < meta.getColumnCount(); i++) {
            String colName = meta.getColumnLabel(i + 1);
            int columnType = meta.getColumnType(i + 1);
            Object o = rs.getObject(i + 1);
            Object value = TypeUtil.convertDatabaseValue(columnType, o);
            row.put(colName, value);
        }
        return row;
    }

    /**
     * 读取查询结果并包装
     *
     * @param rs            查询结果
     * @param clazz         包装类。如果为空则表示用 Map 包装。
     * @param startPosition 开始位置（0 表示第一条记录）
     * @param endPosition   结束位置（不包含）
     *
     * @return 包装好的查询结果。如果 startPosition &lt; 0 或 endPosition &lt; 0 则表示返回所有的查询结果
     *
     * @throws java.sql.SQLException 如果查询失败
     */
    public static List<Object> readResultSet(
            ResultSet rs, Class clazz, NameConverter nameConverter,
            int startPosition, int endPosition) throws Exception {

        ArrayList<Object> result = new ArrayList<Object>();

        // startPosition 是指向要读取的第一条记录之前的位置
        if (startPosition > 0) {
            rs.absolute(startPosition);
        } else if (startPosition == 0) {
            resetRsPosition(rs);
        }

        int counter = startPosition;
        while (rs.next() && (startPosition < 0 || endPosition < 0 || counter < endPosition)) {
            Map row = readRow(rs);

            // 如果是包含分页字段，则去掉
            row.remove(PAGINATION_WRAPPER_COLUMN_NAME);

            result.add(row);
            counter++;
        }

        return clazz == null ? result : TypeConverter.convert(clazz, result, nameConverter);
    }

    // 将 ResultSet 扫描位置重置为第0位
    private static void resetRsPosition(ResultSet rs) throws SQLException {
        try {
            if (rs.getType() == ResultSet.TYPE_FORWARD_ONLY) {
                return;
            }
            rs.beforeFirst();
        } catch (SQLFeatureNotSupportedException e) {
            // just ignore it
        }
    }

    /**
     * 获取分页查询结果
     *
     * @param rs        查询结果
     * @param clazz     包装类
     * @param pageSize  页大小。如果小于 0 则表示取所有记录
     * @param pageIndex 页号
     *
     * @return 查询结果
     *
     * @throws java.sql.SQLException 如果查询失败
     */
    public static Page readPageResultSet(
            ResultSet rs, Class clazz, NameConverter nameConverter,
            int pageSize, int pageIndex) throws Exception {

        Page result = new Page();

        int startPos = pageSize < 0 ? -1 : pageIndex * pageSize;
        int endPos = startPos + pageSize;

        result.addAll(readResultSet(rs, clazz,  nameConverter, startPos, endPos));

        return result;
    }

    public static List<Row> readResultSet(ResultSet rs) throws Exception {
        List<Object> list = readResultSet(rs, null, NameConverter.DEFAULT, -1, -1);
        return list.stream().map(o -> (Row)o).collect(Collectors.toList());
    }
}

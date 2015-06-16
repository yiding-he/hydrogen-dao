package com.hyd.dao.database;

import com.hyd.dao.DAOException;
import com.hyd.dao.Row;
import com.hyd.dao.log.Logger;
import com.hyd.dao.util.ResultSetUtil;

import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * <p>查询结果迭代器。当查询返回大量结果，又没有足够的内存进行缓存时，可以使用 DAO.queryIterator
 * 方法。该方法返回一个迭代器，用来每次获取一行查询结果。当处理完毕后，请务必记得将其关闭。</p>
 * <p>使用示例：<br/><code>
 * RowIterator it;<br>
 * try {<br>
 * &nbsp;&nbsp;&nbsp; it&nbsp;= dao.queryIterator("select
 * * from tt_test");<br>
 * &nbsp;&nbsp;&nbsp; while(it.next()) {<br>
 * &nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp; Map
 * row = it.getRow();<br>
 * &nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;
 * System.out.println("name = " + row.get("name"));<br>
 * &nbsp;&nbsp;&nbsp; }<br>
 * } finally {<br>
 * &nbsp;&nbsp;&nbsp; if (it != null) {<br>
 * &nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;
 * it.close();<br>
 * &nbsp;&nbsp;&nbsp; }<br>
 * }
 * </code></p>
 */
public class RowIterator implements Closeable {

    static final Logger LOG = Logger.getLogger(RowIterator.class);

    private ResultSet rs;

    public RowIterator(ResultSet rs) {
        this.rs = rs;
    }

    /**
     * 获得是否还有查询结果
     *
     * @return 如果还有查询结果则返回 true，并移至下一行
     */
    public boolean next() {
        try {
            return rs.next();
        } catch (SQLException e) {
            throw new DAOException("failed to read next record", e);
        }
    }

    /**
     * 获得查询结果中的当前行
     *
     * @return 当前行的内容
     */
    public Row getRow() {
        try {
            return ResultSetUtil.readRow(rs);
        } catch (IOException e) {
            throw new DAOException("failed to read record", e);
        } catch (SQLException e) {
            throw new DAOException("failed to read record", e);
        }
    }

    @Override
    public void close() {
        if (rs != null) {
            Statement st;
            Connection conn;
            try {
                // 如果先执行 rs.close()，那么 st 和 conn 就会为 null。
                st = rs.getStatement();
                conn = st.getConnection();
            } catch (SQLException e) {
                LOG.warn(e.getMessage(), e);
                return;
            } finally {
                try {
                    rs.close();
                } catch (SQLException e) {
                    LOG.warn(e.getMessage(), e);
                }
            }
            try {
                st.close();
            } catch (SQLException e) {
                LOG.warn(e.getMessage(), e);
            }
            try {
                conn.close();
            } catch (SQLException e) {
                LOG.warn(e.getMessage(), e);
            }
        }
    }
}

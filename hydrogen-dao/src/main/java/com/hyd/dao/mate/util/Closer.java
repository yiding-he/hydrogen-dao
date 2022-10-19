package com.hyd.dao.mate.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Closer {

    public static boolean isClosed(ResultSet rs) {
        try {
            return rs.isClosed();
        } catch (SQLException e) {
            return true;
        }
    }

    public static void closeResultSet(ResultSet rs) {
        if (rs == null || isClosed(rs)) {
            return;
        }

        try {
            var st = rs.getStatement();
            closeStatement(st);
        } catch (SQLException e) {
            // ignore this
        }

        try {
            rs.close();
        } catch (SQLException e) {
            // ignore this
        }
    }

    private static void closeStatement(Statement st) throws SQLException {
        if (st != null && !st.isClosed()) {
            try {
                var conn = st.getConnection();
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                // ignore this
            }
            st.close();
        }
    }

}

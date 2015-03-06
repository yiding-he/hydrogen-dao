package com.hyd.dao.database.commandbuilder.helper;

import java.sql.Connection;

/**
 * (description)
 *
 * @author yiding.he
 */
public class MySqlCommandBuilderHelper extends CommandBuilderHelper {

    public MySqlCommandBuilderHelper(Connection conn) {
        super(conn);
        this.columnMeta = ColumnMeta.MySQL;
    }


}

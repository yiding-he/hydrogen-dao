package com.hyd.dao.src.fx;

import com.hyd.dao.database.ColumnInfo;
import com.hyd.dao.database.DatabaseType;
import com.hyd.dao.src.code.method.QueryListMethodBuilder;
import javafx.stage.Stage;

import java.util.ArrayList;

/**
 * @author yiding.he
 */
public class AddQueryListMethodDialog extends AddQueryOneMethodDialog {

    public AddQueryListMethodDialog(Stage owner, DatabaseType databaseType, String tableName, ColumnInfo[] columns) {
        super(owner, databaseType, tableName, columns);
    }

    @Override
    protected void onOK() {
        QueryListMethodBuilder methodBuilder = new QueryListMethodBuilder(
                databaseType, tableName,
                txtMethodName.getText(), new ArrayList<>(parametersList.getItems())
        );

        setResult(methodBuilder.build());
    }
}

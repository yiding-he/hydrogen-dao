package com.hyd.dao.src.fx;

import com.hyd.dao.database.ColumnInfo;
import com.hyd.dao.database.DatabaseType;
import com.hyd.dao.src.code.method.QueryPageMethodBuilder;
import java.util.ArrayList;
import javafx.stage.Stage;

/**
 * @author yiding.he
 */
public class AddQueryPageMethodDialog extends AddQueryOneMethodDialog {

    AddQueryPageMethodDialog(Stage owner, DatabaseType databaseType, String tableName, ColumnInfo[] columns) {
        super(owner, databaseType, tableName, columns);
    }

    @Override
    protected void onOK() {
        QueryPageMethodBuilder methodBuilder = new QueryPageMethodBuilder(
                databaseType, tableName,
                txtMethodName.getText(), new ArrayList<>(parametersList.getItems())
        );

        setResult(methodBuilder.build());
    }

}

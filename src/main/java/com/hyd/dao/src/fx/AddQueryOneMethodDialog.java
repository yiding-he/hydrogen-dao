package com.hyd.dao.src.fx;

import com.hyd.dao.database.ColumnInfo;
import com.hyd.dao.database.DatabaseType;
import com.hyd.dao.src.RepoMethodDef;
import com.hyd.dao.src.code.ParamInfo;
import com.hyd.dao.src.code.method.QueryOneMethodBuilder;
import com.hyd.dao.src.fx.Fx.*;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Arrays;

import static com.hyd.dao.src.fx.Fx.*;

/**
 * (description)
 * created at 2018/4/12
 *
 * @author yidin
 */
public class AddQueryOneMethodDialog extends Dialog<RepoMethodDef> {

    ListView<ParamInfo> parametersList;

    private ColumnInfo[] columns;

    private ParamInfo currentParamInfo;

    private Form<ParamInfo> methodInfoForm;

    DatabaseType databaseType;

    TextField txtMethodName;

    String tableName;

    public AddQueryOneMethodDialog(Stage owner, DatabaseType databaseType, String tableName, ColumnInfo[] columns) {
        super(owner);
        this.databaseType = databaseType;
        this.columns = columns;
        this.tableName = tableName;
        setTitle("Query One for Table '" + tableName + "'");

        initControls();
    }

    private void initControls() {
        parametersList = new ListView<>();
        Fx.setListViewContent(parametersList, ParamInfo::toString);
    }

    @Override
    protected Parent getBodyRoot() {

        methodInfoForm = Fx.form(100, Arrays.asList(
                comboField("Column:", info -> info.columnInfo, ColumnInfo::getColumnName, columns),
                comboField("Comparator:", info -> info.comparator, Comparator::getSymbol, Comparator.values())
        ));

        txtMethodName = new TextField();

        VBox root = vbox(Expand.LastExpand, 0, PADDING,
                hbox(Expand.LastExpand, Pos.BASELINE_LEFT, PADDING, PADDING,
                        new Label("Method Name:"),
                        txtMethodName
                ),
                hbox(Expand.AllExpand, PADDING, PADDING,
                        vbox(Expand.LastExpand, 0, PADDING,
                                new Label("Parameters:"),
                                parametersList
                        ),
                        vbox(Expand.FirstExpand, 0, PADDING,
                                titledPane(80, "Add Parameter", methodInfoForm),
                                button("Add/Update", this::addOrUpdateParamInfo)
                        )
                )
        );

        root.setPrefHeight(300);
        return root;
    }

    private void addOrUpdateParamInfo() {
        ObservableList<ParamInfo> items = parametersList.getItems();
        if (!items.contains(currentParamInfo)) {
            items.add(currentParamInfo);
            currentParamInfo = new ParamInfo();
            methodInfoForm.load(currentParamInfo);
        }
    }

    @Override
    protected void onOK() {

        QueryOneMethodBuilder methodBuilder = new QueryOneMethodBuilder(
                databaseType, tableName,
                txtMethodName.getText(), new ArrayList<>(parametersList.getItems())
        );

        setResult(methodBuilder.build());
    }

    @Override
    protected void onCancel() {
        setResult(null);
    }

    @Override
    protected void onShown() {
        currentParamInfo = new ParamInfo();
        methodInfoForm.load(currentParamInfo);
    }

    //////////////////////////////////////////////////////////////

}

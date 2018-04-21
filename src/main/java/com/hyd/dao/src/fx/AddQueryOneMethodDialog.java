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

    AddQueryOneMethodDialog(
            Stage owner, DatabaseType databaseType, String tableName, ColumnInfo[] columns) {

        super(owner);
        this.databaseType = databaseType;
        this.columns = columns;
        this.tableName = tableName;
        setTitle("表 '" + tableName + "' 的查询条件");

        initControls();
    }

    private void initControls() {
        parametersList = new ListView<>();
        Fx.setListViewContent(parametersList, ParamInfo::toString);
    }

    @Override
    protected Parent getBodyRoot() {

        methodInfoForm = Fx.form(100, Arrays.asList(
                comboField("字段:", info -> info.columnInfo, ColumnInfo::getColumnName, columns),
                comboField("操作符:", info -> info.comparator, Comparator::getSymbol, Comparator.values())
        ));

        txtMethodName = new TextField();

        VBox root = vbox(Expand.LastExpand, 0, PADDING,
                hbox(Expand.LastExpand, Pos.BASELINE_LEFT, PADDING, PADDING,
                        new Label("方法名称:"),
                        txtMethodName
                ),
                hbox(Expand.AllExpand, PADDING, PADDING,
                        vbox(Expand.LastExpand, 0, PADDING,
                                new Label("查询参数列表:"),
                                parametersList,
                                button("Delete", this::deleteParam)
                        ),
                        vbox(Expand.FirstExpand, 0, PADDING,
                                titledPane(80, "添加参数", methodInfoForm),
                                button("添加", this::addOrUpdateParamInfo)
                        )
                )
        );

        root.setPrefHeight(300);
        return root;
    }

    private void deleteParam() {
        ParamInfo selectedItem = parametersList.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            parametersList.getItems().remove(selectedItem);
        }
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
}

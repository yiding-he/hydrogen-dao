package com.hyd.dao.src.fx;

import com.hyd.dao.database.ColumnInfo;
import com.hyd.dao.database.DatabaseType;
import com.hyd.dao.src.RepoMethodDef;
import com.hyd.dao.src.RepoMethodReturnType;
import com.hyd.dao.src.code.AccessType;
import com.hyd.dao.src.code.MethodArg;
import com.hyd.dao.src.fx.Fx.*;
import com.hyd.dao.util.Str;
import com.hyd.dao.util.TypeUtil;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.stream.Collectors;

import static com.hyd.dao.src.fx.Fx.*;

/**
 * (description)
 * created at 2018/4/12
 *
 * @author yidin
 */
public class AddQueryOneMethodDialog extends Dialog<RepoMethodDef> {

    private ListView<ParamInfo> parametersList;

    private ColumnInfo[] columns;

    private ParamInfo currentParamInfo;

    private Form<ParamInfo> methodInfoForm;

    private DatabaseType databaseType;

    private TextField txtMethodName;

    private String tableName;

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

        return vbox(Expand.LastExpand, 0, PADDING,
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
        RepoMethodDef repoMethodDef = new RepoMethodDef();
        repoMethodDef.access = AccessType.Public;
        repoMethodDef.name = txtMethodName.getText();
        repoMethodDef.returnType = RepoMethodReturnType.Single;
        repoMethodDef.type = Str.underscore2Class(tableName);

        parametersList.getItems().forEach(paramInfo -> repoMethodDef.args.add(parseParamInfo(paramInfo)));

        if (Str.isEmptyString(repoMethodDef.name)) {
            repoMethodDef.name = "queryBy" + repoMethodDef.args.stream()
                    .map(arg -> Str.capitalize(arg.name))
                    .collect(Collectors.joining("And"));
        }

        setResult(repoMethodDef);
    }

    private MethodArg parseParamInfo(ParamInfo paramInfo) {
        ColumnInfo columnInfo = paramInfo.columnInfo.get();
        return new MethodArg(
                TypeUtil.getJavaType(databaseType, columnInfo.getDataType()),
                Str.underscore2Property(columnInfo.getColumnName())
        );
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

    private static class ParamInfo {

        public ObjectProperty<ColumnInfo> columnInfo = new SimpleObjectProperty<>();

        public ObjectProperty<Comparator> comparator = new SimpleObjectProperty<>();

        @Override
        public String toString() {
            return columnInfo.get().getColumnName() + " " + comparator.get().getSymbol() + " ?";
        }
    }
}

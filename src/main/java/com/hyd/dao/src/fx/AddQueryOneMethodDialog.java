package com.hyd.dao.src.fx;

import com.hyd.dao.database.ColumnInfo;
import com.hyd.dao.src.MethodDef;
import com.hyd.dao.src.RepoMethodInfo;
import com.hyd.dao.src.RepoMethodType;
import com.hyd.dao.src.fx.Fx.Expand;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.util.Arrays;

import static com.hyd.dao.src.fx.Fx.PADDING;
import static com.hyd.dao.src.fx.Fx.hbox;
import static com.hyd.dao.src.fx.Fx.vbox;

/**
 * (description)
 * created at 2018/4/12
 *
 * @author yidin
 */
public class AddQueryOneMethodDialog extends Dialog<MethodDef> {

    private static ListView<ColumnInfo> columnInfoListView;

    private ColumnInfo[] columns;

    public AddQueryOneMethodDialog(Stage owner, ColumnInfo[] columns) {
        super(owner, root());
        this.columns = columns;
        setTitle("Add Query One Method");

        initControls();
    }

    private void initControls() {
        Fx.setListViewContent(columnInfoListView, ColumnInfo::getColumnName);
        columnInfoListView.getItems().addAll(columns);
    }

    private static Parent root() {
        columnInfoListView = new ListView<>();

        return hbox(Expand.AllExpand, PADDING, PADDING,
                vbox(Expand.LastExpand, 0, PADDING,
                        new Label("Query Fields"),
                        columnInfoListView
                ),
                vbox(Expand.LastExpand, 0, PADDING,
                        new Label("Condition Fields"),
                        new ListView<String>()
                )
        );
    }

    @Override
    protected void onOK() {

    }

    @Override
    protected void onCancel() {

    }
}

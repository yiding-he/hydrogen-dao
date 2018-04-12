package com.hyd.dao.src.fx;

import com.hyd.dao.src.MethodDef;
import javafx.scene.Parent;
import javafx.stage.Stage;

/**
 * (description)
 * created at 2018/4/12
 *
 * @author yidin
 */
public class AddMethodDialog extends Dialog<MethodDef> {

    public AddMethodDialog(Stage owner) {
        super(owner, root());
        setTitle("Add Repository Method");
    }

    private static Parent root() {
        return null;
    }

    @Override
    protected void onOK() {

    }

    @Override
    protected void onCancel() {

    }
}

package com.hyd.dao.src.fx;

import com.hyd.dao.src.MethodDef;
import com.hyd.dao.src.RepoMethodInfo;
import com.hyd.dao.src.RepoMethodType;
import javafx.scene.Parent;
import javafx.stage.Stage;

import java.util.Arrays;

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
        return Fx.form(100, Arrays.asList(
                Fx.comboField("Method Type", RepoMethodInfo::typeProperty, RepoMethodType.values())
        ));
    }

    @Override
    protected void onOK() {

    }

    @Override
    protected void onCancel() {

    }
}

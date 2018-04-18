package com.hyd.dao.src.fx;

import com.hyd.dao.src.fx.Fx.*;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.function.Supplier;

import static com.hyd.dao.src.fx.Fx.*;

/**
 * (description)
 * created at 2018/4/12
 *
 * @author yidin
 */
public abstract class Dialog<T> {

    private Stage owner;

    private Supplier<Parent> rootSupplier;

    private T result;

    private Stage stage;

    private String title;

    public Dialog(Stage owner) {
        this.owner = owner;
        this.rootSupplier = () ->
                vbox(Expand.FirstExpand, PADDING, PADDING,
                        getBodyRoot(),
                        new Separator(),
                        hbox(Expand.FirstExpand, PADDING, PADDING,
                                new Pane(),
                                okButton(),
                                cancelButton()
                        )
                );
    }

    protected abstract Parent getBodyRoot();

    protected void setTitle(String title) {
        this.title = title;
    }

    protected void setResult(T result) {
        this.result = result;
    }

    private Button cancelButton() {
        Button button = button("Cancel", this::cancelClicked);
        button.setPrefWidth(80);
        return button;
    }

    private Button okButton() {
        Button button = button("OK", this::okClicked);
        button.setPrefWidth(80);
        return button;
    }

    private void okClicked() {
        this.onOK();
        if (this.stage != null) {
            this.stage.close();
        }
    }

    private void cancelClicked() {
        this.onCancel();
        if (this.stage != null) {
            this.stage.close();
        }
    }

    protected abstract void onOK();

    protected abstract void onCancel();

    protected void onShown() {

    }

    public T show() {
        this.stage = new Stage();
        this.stage.initOwner(this.owner);
        this.stage.initModality(Modality.WINDOW_MODAL);
        this.stage.setScene(new Scene(this.rootSupplier.get()));
        this.stage.setTitle(this.title);
        this.stage.setOnShown(event -> this.onShown());
        this.stage.showAndWait();
        return result;
    }
}

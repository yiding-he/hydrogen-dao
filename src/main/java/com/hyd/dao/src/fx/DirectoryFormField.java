package com.hyd.dao.src.fx;

import javafx.beans.property.StringProperty;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.util.function.Function;

public class DirectoryFormField<T> extends FormField<T> {

    private TextField textField;

    private Button button;

    private Function<T, StringProperty> extractor;

    private StringProperty currentBind;

    public DirectoryFormField(String text, Function<T, StringProperty> extractor) {
        super(text);

        this.extractor = extractor;
        this.textField = new TextField();
        this.textField.setEditable(false);
        this.button = new Button("...");
        this.button.setOnAction(event -> chooseDirectory());

        this.getChildren().addAll(textField, button);
        this.expand(this.textField);
    }

    private void chooseDirectory() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(new File("."));
        File dir = directoryChooser.showDialog(CodeGeneratorApp.getPrimaryStage());
        if (dir != null) {
            this.textField.setText(dir.getAbsolutePath());
        }
    }

    @Override
    public void readFrom(T t) {
        if (this.currentBind != null) {
            this.textField.textProperty().unbindBidirectional(this.currentBind);
        }

        if (t == null) {
            this.textField.setText(null);
        } else {
            this.currentBind = this.extractor.apply(t);
            this.textField.textProperty().bindBidirectional(this.currentBind);
        }
    }
}

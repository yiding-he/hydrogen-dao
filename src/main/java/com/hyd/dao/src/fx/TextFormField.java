package com.hyd.dao.src.fx;

import javafx.beans.property.StringProperty;
import javafx.scene.control.TextField;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author yiding_he
 */
public class TextFormField<T> extends FormField<T> {

    private TextField textField;

    private Function<T, StringProperty> extractor;

    private StringProperty currentBind;

    public TextFormField(String text, Function<T, StringProperty> extractor) {
        super(text);

        this.textField = new TextField();
        this.extractor = extractor;

        this.getChildren().add(textField);
        this.expand(this.textField);
    }

    @Override
    public void readFrom(T t) {

        if (this.currentBind != null) {
            this.textField.textProperty().unbindBidirectional(this.currentBind);
        }

        if (t != null) {
            this.currentBind = this.extractor.apply(t);
            this.textField.textProperty().bindBidirectional(this.currentBind);
        } else {
            this.textField.setText(null);
        }
    }

    public void setOnTextChanged(Consumer<String> onTextChanged) {
        if (onTextChanged != null) {
            this.textField.textProperty().addListener(
                    (ob, oldValue, newValue) -> onTextChanged.accept(newValue));
        }
    }
}

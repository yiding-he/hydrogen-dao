package com.hyd.dao.src.fx;

import javafx.beans.property.StringProperty;
import javafx.scene.control.TextField;

import java.util.function.Function;

/**
 * @author yiding_he
 */
public class TextFormField<T> extends FormField<T> {

    private TextField textField;

    private Function<T, StringProperty> extractor;

    public TextFormField(String text, Function<T, StringProperty> extractor) {
        super(text);

        this.textField = new TextField();
        this.extractor = extractor;

        this.getChildren().add(textField);
        this.expand(this.textField);
    }

    @Override
    public void readFrom(T t) {
        this.textField.textProperty().bind(this.extractor.apply(t));
    }
}

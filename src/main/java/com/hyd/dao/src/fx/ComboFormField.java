package com.hyd.dao.src.fx;

import javafx.beans.property.ObjectProperty;
import javafx.scene.control.ComboBox;

import java.util.function.Function;

/**
 * (description)
 * created at 2018/4/12
 *
 * @author yidin
 */
public class ComboFormField<T, C> extends FormField<T> {

    private ComboBox<C> comboBox;

    private Function<T, ObjectProperty<C>> extractor;

    public ComboFormField(String text, Function<T, ObjectProperty<C>> extractor, C[] values) {
        super(text);

        this.comboBox = new ComboBox<>();
        this.comboBox.getItems().addAll(values);
        this.comboBox.getSelectionModel().select(0);
        this.extractor = extractor;

        this.getChildren().add(comboBox);
        this.expand(this.comboBox);
    }

    @Override
    public void readFrom(T t) {

    }
}

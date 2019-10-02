package com.hyd.dao.src.fx;

import java.util.function.Function;
import javafx.beans.property.ObjectProperty;
import javafx.scene.control.ComboBox;

/**
 * (description)
 * created at 2018/4/12
 *
 * @author yidin
 */
public class ComboFormField<T, C> extends FormField<T> {

    private ComboBox<C> comboBox;

    private Function<T, ObjectProperty<C>> extractor;

    private ObjectProperty<C> currentBind;

    public ComboFormField(String text, Function<T, ObjectProperty<C>> extractor, C[] values) {
        super(text);

        this.comboBox = new ComboBox<>();
        this.comboBox.getItems().addAll(values);
        this.comboBox.getSelectionModel().select(0);
        this.extractor = extractor;

        this.getChildren().add(comboBox);
        this.expand(this.comboBox);
    }

    public ComboBox<C> getComboBox() {
        return comboBox;
    }

    @Override
    public void readFrom(T t) {

        if (currentBind != null) {
            this.getComboBox().valueProperty().unbindBidirectional(currentBind);
        }

        if (t != null) {
            ObjectProperty<C> property = extractor.apply(t);
            if (property.get() == null) {
                property.setValue(this.getComboBox().getSelectionModel().getSelectedItem());
            }

            this.getComboBox().valueProperty().bindBidirectional(property);
            currentBind = property;
        }
    }
}

package com.hyd.dao.src.fx;

import javafx.geometry.Insets;
import javafx.scene.layout.VBox;

import java.util.List;

/**
 * @author yiding_he
 */
public class Form<T> extends VBox {

    private List<FormField<T>> fields;

    public Form(int labelWidth, List<FormField<T>> fields) {
        this.fields = fields;

        this.setSpacing(Fx.PADDING);
        this.setPadding(new Insets(Fx.PADDING));
        this.getChildren().addAll(fields);

        for (FormField<T> field : fields) {
            field.setLabelWidth(labelWidth);
        }
    }

    public void load(T t) {
        for (FormField<T> field : fields) {
            field.readFrom(t);
        }
    }

}

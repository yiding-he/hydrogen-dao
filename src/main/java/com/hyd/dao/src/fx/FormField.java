package com.hyd.dao.src.fx;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

/**
 * @author yiding_he
 */
public abstract class FormField<T> extends HBox {

    private Label label;

    public FormField(String text) {
        this.label = new Label(text);
        this.label.setMinWidth(Region.USE_COMPUTED_SIZE);

        this.setAlignment(Pos.BASELINE_LEFT);
        this.setSpacing(Fx.PADDING);
        this.getChildren().add(this.label);
    }

    public abstract void readFrom(T t);

    protected void expand(Node node) {
        HBox.setHgrow(node, Priority.ALWAYS);
    }

    public void setLabelWidth(int labelWidth) {
        this.label.setMinWidth(labelWidth);
    }
}

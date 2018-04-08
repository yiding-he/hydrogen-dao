package com.hyd.dao.src.fx;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * (description)
 * created at 2018/4/8
 *
 * @author yidin
 */
public class Fx {

    public static TitledPane titledPane(int prefHeight, String title, Node content) {
        TitledPane titledPane = new TitledPane(title, content);
        titledPane.setCollapsible(false);
        titledPane.setMaxHeight(Double.POSITIVE_INFINITY);
        if (prefHeight > 0) {
            titledPane.setPrefHeight(prefHeight);
        }
        return titledPane;
    }

    public static Button button(String text, Runnable action) {
        Button button = new Button(text);
        button.setOnAction(event -> action.run());
        return button;
    }

    public static HBox hbox(Node... children) {
        return hbox(10, children);
    }

    public static HBox hbox(int paddingAndSpacing, Node... children) {
        return hbox(paddingAndSpacing, paddingAndSpacing, children);
    }

    public static HBox hbox(int padding, int spacing, Node... children) {
        HBox hbox = new HBox(spacing, children);
        hbox.setPadding(new Insets(padding));
        return hbox;
    }

    public static VBox vbox(int padding, int spacing, Node... children) {
        VBox vbox = new VBox(spacing, children);
        vbox.setPadding(new Insets(padding));
        vbox.setStyle("-fx-background-color: #ddddaa");

        if (children.length > 0) {
            VBox.setVgrow(children[children.length - 1], Priority.ALWAYS);
        }

        return vbox;
    }
}

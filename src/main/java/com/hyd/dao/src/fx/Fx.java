package com.hyd.dao.src.fx;

import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.*;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * (description)
 * created at 2018/4/8
 *
 * @author yidin
 */
public class Fx {

    public static final int PADDING = 7;

    public enum Expand {
        FirstExpand, LastExpand, NoExpand
    }

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
        button.setMinWidth(Region.USE_COMPUTED_SIZE);
        button.setOnAction(event -> action.run());
        return button;
    }

    public static HBox hbox(Expand expand, int padding, int spacing, Node... children) {
        HBox hbox = new HBox(spacing, children);
        hbox.setPadding(new Insets(padding));

        if (children.length > 0) {
            if (expand == Expand.LastExpand) {
                HBox.setHgrow(children[children.length - 1], Priority.ALWAYS);
            } else if (expand == Expand.FirstExpand) {
                HBox.setHgrow(children[0], Priority.ALWAYS);
            } else {
                for (Node child : children) {
                    if (child.getClass() == Pane.class) {
                        HBox.setHgrow(child, Priority.ALWAYS);
                        break;
                    }
                }
            }
        }

        return hbox;
    }

    public static VBox vbox(Expand expand, int padding, int spacing, Node... children) {
        VBox vbox = new VBox(spacing, children);
        vbox.setPadding(new Insets(padding));

        if (children.length > 0) {
            if (expand == Expand.LastExpand) {
                VBox.setVgrow(children[children.length - 1], Priority.ALWAYS);
            } else if (expand == Expand.FirstExpand) {
                VBox.setVgrow(children[0], Priority.ALWAYS);
            }
        }

        return vbox;
    }

    public static <T> void setListViewContent(ListView<T> listView, Function<T, String> toString) {
        listView.setCellFactory(lv -> new ListCell<T>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(toString.apply(item));
                }
            }
        });
    }

    public static <T> void setListViewSelectionChanged(ListView<T> listView, Consumer<T> onSelected) {
        if (onSelected != null) {
            listView.getSelectionModel().selectedItemProperty()
                    .addListener((ob, oldValue, newValue) -> onSelected.accept(newValue));
        }
    }

    public static <T> Form<T> form(int labelWidth, List<FormField<T>> fields) {
        return new Form<>(labelWidth, fields);
    }

    public static <T> TextFormField<T> textField(String text, Function<T, StringProperty> extractor) {
        return new TextFormField<>(text, extractor);
    }

    public static ButtonType alert(AlertType alertType, String title, String message, ButtonType... buttons) {
        Alert alert = new Alert(alertType, message, buttons);
        alert.setTitle(title);
        alert.setHeaderText(null);
        return alert.showAndWait().orElse(ButtonType.CANCEL);
    }

    public static boolean confirm(String message) {
        return alert(AlertType.CONFIRMATION, "Confirm", message,
                ButtonType.YES, ButtonType.NO) == ButtonType.YES;
    }

    public static void error(Throwable throwable) {
        alert(AlertType.ERROR, "Error", throwable.toString(), ButtonType.OK);
    }

    public static MenuItem menuItem(String text, Runnable onAction) {
        MenuItem menuItem = new MenuItem(text);
        menuItem.setOnAction(event -> onAction.run());
        return menuItem;
    }
}

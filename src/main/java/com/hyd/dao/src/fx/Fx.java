package com.hyd.dao.src.fx;

import com.hyd.dao.log.Logger;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.*;

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

    private static final Logger LOG = Logger.getLogger(Fx.class);

    public static final int PADDING = 7;

    public enum Expand {
        FirstExpand, LastExpand, NoExpand, NthExpand, AllExpand;

        private int n;

        public Expand set(int n) {
            if (this != NthExpand) {
                throw new IllegalArgumentException("Only NthExpand can use this method.");
            }
            this.n = n;
            return this;
        }

        public int getN() {
            if (this != NthExpand) {
                throw new IllegalArgumentException("Only NthExpand can use this method.");
            }
            return n;
        }
    }

    public static Pane pane(int width, int height) {
        Pane pane = new Pane();
        if (width > 0) {
            pane.setPrefWidth(width);
        }
        if (height > 0) {
            pane.setPrefHeight(height);
        }
        return pane;
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

    public static TabPane tabPane(Tab... tabs) {
        return new TabPane(tabs);
    }

    public static Tab tab(String text, Node content) {
        Tab tab = new Tab(text, content);
        tab.setClosable(false);
        return tab;
    }

    public static Button button(String text, Runnable action) {
        Button button = new Button(text);
        button.setMinWidth(Region.USE_COMPUTED_SIZE);
        if (action != null) {
            button.setOnAction(event -> action.run());
        }
        return button;
    }

    public static HBox hbox(Expand expand, int padding, int spacing, Node... children) {
        return hbox(expand, null, padding, spacing, children);
    }

    public static HBox hbox(Expand expand, Pos alignment, int padding, int spacing, Node... children) {
        HBox hbox = new HBox(spacing, children);
        hbox.setPadding(new Insets(padding));

        if (alignment != null) {
            hbox.setAlignment(alignment);
        }

        if (children.length > 0) {
            if (expand == Expand.LastExpand) {
                HBox.setHgrow(children[children.length - 1], Priority.ALWAYS);
            } else if (expand == Expand.FirstExpand) {
                HBox.setHgrow(children[0], Priority.ALWAYS);
            } else if (expand == Expand.NthExpand) {
                int n = expand.getN();
                int index = n < 0 ? (children.length + n) : n;
                HBox.setHgrow(children[index], Priority.ALWAYS);
            } else if (expand == Expand.AllExpand) {
                for (Node child : children) {
                    HBox.setHgrow(child, Priority.ALWAYS);
                }
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
            } else if (expand == Expand.NthExpand) {
                int n = expand.getN();
                int index = n < 0 ? (children.length + n) : n;
                VBox.setVgrow(children[index], Priority.ALWAYS);
            }
        }

        return vbox;
    }

    public static <S, T> TableColumn<S, T> column(String text, Function<S, T> parser) {
        TableColumn<S, T> column = new TableColumn<>(text);
        column.setCellValueFactory(features -> {
            S value = features.getValue();
            return new SimpleObjectProperty<>(parser.apply(value));
        });
        return column;
    }

    public static <T> void setListViewContent(ListView<T> listView, Function<T, String> toString) {
        listView.setCellFactory(lv -> createCell(toString));
    }

    public static <T> void setListViewSelectionChanged(ListView<T> listView, Consumer<T> onSelected) {
        if (onSelected != null) {
            listView.getSelectionModel().selectedItemProperty()
                    .addListener((ob, oldValue, newValue) -> onSelected.accept(newValue));
        }
    }

    public static <T> void setComboBoxContent(ComboBox<T> comboBox, Function<T, String> toString) {
        if (comboBox != null) {
            comboBox.setButtonCell(createCell(toString));
            comboBox.setCellFactory(combo -> createCell(toString));
        }
    }

    private static <T> ListCell<T> createCell(Function<T, String> toString) {
        return new ListCell<T>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(toString.apply(item));
                }
            }
        };
    }

    public static <T> Form<T> form(int labelWidth, List<FormField<T>> fields) {
        return new Form<>(labelWidth, fields);
    }

    public static <T> TextFormField<T> textField(String text, Function<T, StringProperty> extractor) {
        return new TextFormField<>(text, extractor);
    }

    public static <T, C> ComboFormField<T, C> comboField(
            String text, Function<T, ObjectProperty<C>> extractor, C[] values) {
        return new ComboFormField<>(text, extractor, values);
    }

    public static <T, C> ComboFormField<T, C> comboField(
            String text, Function<T, ObjectProperty<C>> extractor, Function<C, String> toString, C[] values) {
        ComboFormField<T, C> field = new ComboFormField<>(text, extractor, values);
        setComboBoxContent(field.getComboBox(), toString);
        return field;
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
        LOG.error("", throwable);
        alert(AlertType.ERROR, "Error", throwable.toString(), ButtonType.OK);
    }

    public static void error(String message) {
        alert(AlertType.ERROR, "Error", message, ButtonType.OK);
    }

    public static MenuItem menuItem(String text, Runnable onAction) {
        MenuItem menuItem = new MenuItem(text);
        if (onAction != null) {
            menuItem.setOnAction(event -> onAction.run());
        } else {
            menuItem.setDisable(true);
        }
        return menuItem;
    }

    public static MenuItem menuItem(String text, String shortcut, Runnable onAction) {
        MenuItem menuItem = new MenuItem(text);
        menuItem.setAccelerator(KeyCombination.valueOf(shortcut));
        menuItem.setOnAction(event -> onAction.run());
        return menuItem;
    }

    public static MenuButton menuButton(String text, MenuItem... items) {
        return new MenuButton(text, null, items);
    }
}

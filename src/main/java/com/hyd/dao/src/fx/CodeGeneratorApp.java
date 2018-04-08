package com.hyd.dao.src.fx;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import static com.hyd.dao.src.fx.Fx.*;

/**
 * (description)
 * created at 2018/4/8
 *
 * @author yidin
 */
public class CodeGeneratorApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Code Generator");
        primaryStage.setScene(new Scene(root(), 600, 400));
        primaryStage.show();
    }

    private Parent root() {
        return hbox(
                profileList(),
                vbox(0, 0, titledPane(-1, "Tables", new ListView<String>())),
                optionPanel()
        );
    }

    private VBox profileList() {
        return vbox(0, 10, profileListPane(), profileOptionPane());
    }

    private TitledPane profileOptionPane() {
        return titledPane(-1, "Profile Options", new VBox());
    }

    private TitledPane profileListPane() {
        return titledPane(250, "Profiles", vbox(10, 10,
                new ListView<Profile>(),
                hbox(0, 10,
                        button("Create", this::createProfile),
                        button("Delete", this::deleteProfile)
                )
        ));
    }

    private void deleteProfile() {

    }

    private void createProfile() {

    }

    private VBox optionPanel() {
        return new VBox();
    }

    private ListView<String> tableList() {
        return new ListView<>();
    }
}

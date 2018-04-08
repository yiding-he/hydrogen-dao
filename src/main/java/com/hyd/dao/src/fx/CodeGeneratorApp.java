package com.hyd.dao.src.fx;

import com.alibaba.fastjson.JSON;
import com.hyd.dao.log.Logger;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static com.hyd.dao.src.fx.Fx.*;

/**
 * (description)
 * created at 2018/4/8
 *
 * @author yidin
 */
public class CodeGeneratorApp extends Application {

    private static final Logger LOG = Logger.getLogger(CodeGeneratorApp.class);

    private static final Charset CHARSET = Charset.forName("UTF-8");

    private ListView<String> tableNamesList = new ListView<>();

    private ListView<Profile> profileList = new ListView<>();

    @Override
    public void start(Stage primaryStage) throws Exception {
        Scene scene = new Scene(root(), 1000, 700);

        initControls();

        primaryStage.setTitle("Code Generator");
        primaryStage.setScene(scene);
        primaryStage.setOnShown(event -> loadProfiles());
        primaryStage.show();
    }

    private void initControls() {
        setListViewContent(profileList, Profile::getName);

    }

    private void loadProfiles() {
        try {
            Path profilePath = Paths.get("./hydrogen-generator-profiles.json");
            if (Files.exists(profilePath)) {
                String content = new String(Files.readAllBytes(profilePath), CHARSET);

                if (content.length() > 0) {
                    List<Profile> profiles = JSON.parseArray(content, Profile.class);
                    profileList.getItems().addAll(profiles);
                }
            }
        } catch (Exception e) {
            LOG.error("Error reading profiles, please remove this file", e);
        }
    }

    private Parent root() {

        return hbox(Expand.LastExpand, PADDING, PADDING,
                vbox(Expand.FirstExpand, 0, PADDING,
                        titledPane(-1, "Profiles",
                                vbox(Expand.FirstExpand, PADDING, PADDING,
                                        profileList,
                                        hbox(Expand.NoExpand, 0, PADDING,
                                                button("Create", this::createProfile),
                                                button("Delete", this::deleteProfile),
                                                new Pane(),
                                                button("Connect", this::connectProfile)
                                        )
                                )),
                        titledPane(250, "Profile Options", Fx.form(75, Arrays.asList(
                                textField("Name:", Profile::nameProperty),
                                textField("URL:", Profile::urlProperty),
                                textField("Username:", Profile::usernameProperty),
                                textField("Password:", Profile::passwordProperty)
                        )))
                ),
                vbox(Expand.LastExpand, 0, 0,
                        titledPane(-1, "Tables", vbox(Expand.LastExpand, 0, 0, tableNamesList))
                ),
                vbox(Expand.LastExpand, 0, PADDING,
                        titledPane(200, "Repository Class Options", new VBox()),
                        titledPane(-1, "Code", vbox(Expand.LastExpand, 0, 0, new TextArea()))
                )
        );
    }

    private void connectProfile() {

    }

    private void deleteProfile() {

    }

    private void createProfile() {

    }

    private ListView<String> tableList() {
        return new ListView<>();
    }
}

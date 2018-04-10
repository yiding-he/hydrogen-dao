package com.hyd.dao.src.fx;

import com.alibaba.fastjson.JSON;
import com.hyd.dao.database.commandbuilder.helper.CommandBuilderHelper;
import com.hyd.dao.log.Logger;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.DriverManager;
import java.util.Arrays;
import java.util.List;

import static com.hyd.dao.src.fx.Fx.*;
import static com.hyd.dao.src.fx.Fx.Expand.FirstExpand;
import static com.hyd.dao.src.fx.Fx.Expand.LastExpand;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

/**
 * (description)
 * created at 2018/4/8
 *
 * @author yidin
 */
public class CodeGeneratorApp extends Application {

    private static final Logger LOG = Logger.getLogger(CodeGeneratorApp.class);

    private static final Charset CHARSET = Charset.forName("UTF-8");

    public static final String DEFAULT_PROFILE_PATH = "./hydrogen-generator-profiles.json";

    public static final String APP_NAME = "Code Generator";

    ///////////////////////////////////////////////

    private String profilePath = DEFAULT_PROFILE_PATH;

    private ListView<String> tableNamesList = new ListView<>();

    private ListView<Profile> profileList = new ListView<>();

    private Form<Profile> profileForm;

    private TextFormField<Profile> txtProfileName;

    private Stage primaryStage;

    private ConnectionManager connectionManager;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        Scene scene = new Scene(root(), 1000, 700);

        initControls();

        primaryStage.setTitle(APP_NAME);
        primaryStage.setScene(scene);
        primaryStage.setOnShown(event -> loadProfiles());
        primaryStage.show();
    }

    private void initControls() {
        setListViewContent(profileList, Profile::getName);
        setListViewSelectionChanged(profileList, this::onSelectedProfileChanged);
    }

    private void onSelectedProfileChanged(Profile profile) {
        if (profile == null) {
            profileForm.load(null);
        } else {
            profileForm.load(profile);
        }
    }

    private void loadProfiles() {
        try {
            Path profilePath = Paths.get(this.profilePath);
            if (Files.exists(profilePath)) {
                String content = new String(Files.readAllBytes(profilePath), CHARSET);

                if (content.length() > 0) {
                    List<Profile> profiles = JSON.parseArray(content, Profile.class);
                    profileList.getItems().setAll(profiles);
                }
            }

            this.primaryStage.setTitle(APP_NAME + " - " + profilePath);
        } catch (Exception e) {
            LOG.error("Error reading profiles, please remove this file", e);
            error(e);
        }
    }

    private void saveProfiles() {
        try {
            List<Profile> profiles = profileList.getItems();
            byte[] content = JSON.toJSONBytes(profiles);

            Path profilePath = Paths.get(this.profilePath);
            Files.write(profilePath, content, TRUNCATE_EXISTING, CREATE);
        } catch (Exception e) {
            LOG.error("Error writing profiles", e);
            error(e);
        }
    }

    private Parent root() {

        txtProfileName = textField("Name:", Profile::nameProperty);
        txtProfileName.setOnTextChanged(text -> profileList.refresh());

        profileForm = Fx.form(75, Arrays.asList(
                txtProfileName,
                textField("Driver:", Profile::driverProperty),
                textField("URL:", Profile::urlProperty),
                textField("Username:", Profile::usernameProperty),
                textField("Password:", Profile::passwordProperty)
        ));

        Button deleteButton = button("Delete", this::deleteProfile);
        Button connectButton = button("Connect", this::connectProfile);

        BooleanBinding selectedProfileIsNull =
                Bindings.isNull(profileList.getSelectionModel().selectedItemProperty());

        profileForm.disableProperty().bind(selectedProfileIsNull);
        deleteButton.disableProperty().bind(selectedProfileIsNull);
        connectButton.disableProperty().bind(selectedProfileIsNull);

        return vbox(LastExpand, 0, 0,
                new MenuBar(new Menu("_File", null,
                        menuItem("_Open...", "Shortcut+O", this::openFile),
                        menuItem("_Save", "Shortcut+S", this::saveFile),
                        new SeparatorMenuItem(),
                        menuItem("E_xit", this::exit)
                )),
                hbox(LastExpand, PADDING, PADDING,
                        vbox(FirstExpand, 0, PADDING,
                                titledPane(-1, "Profiles",
                                        vbox(FirstExpand, PADDING, PADDING,
                                                profileList,
                                                hbox(Expand.NoExpand, 0, PADDING,
                                                        button("Create", this::createProfile),
                                                        deleteButton,
                                                        new Pane(),
                                                        connectButton
                                                )
                                        )),
                                titledPane(250, "Profile Options", profileForm)
                        ),
                        vbox(LastExpand, 0, 0,
                                titledPane(-1, "Tables",
                                        vbox(LastExpand, 0, 0, tableNamesList))
                        ),
                        vbox(LastExpand, 0, PADDING,
                                titledPane(200, "Repository Class Options", new VBox()),
                                titledPane(-1, "Code",
                                        vbox(LastExpand, 0, 0, new TextArea()))
                        )
                )
        );
    }

    private void exit() {

    }

    private void saveFile() {
        saveProfiles();
    }

    private void openFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("."));
        File selectedFile = fileChooser.showOpenDialog(this.primaryStage);

        if (selectedFile != null) {
            this.profilePath = selectedFile.getAbsolutePath();
            loadProfiles();
        }
    }

    private void connectProfile() {
        Profile selectedItem = profileList.getSelectionModel().getSelectedItem();

        if (StringUtils.isAnyBlank(selectedItem.getDriver(), selectedItem.getUrl())) {
            error("Profile is incomplete.");
            return;
        }

        if (!initConnectionManager(selectedItem)) {
            return;
        }

        loadTables();
    }

    private boolean initConnectionManager(Profile selectedItem) {
        if (connectionManager != null) {
            connectionManager.close();
            connectionManager = null;
        }

        try {
            Class.forName(selectedItem.getDriver());
            connectionManager = new ConnectionManager(() ->
                    DriverManager.getConnection(
                            selectedItem.getUrl(),
                            selectedItem.getUsername(),
                            selectedItem.getPassword()
                    )
            );
        } catch (ClassNotFoundException e) {
            LOG.error("", e);
            error(e);
            return false;
        }

        return true;
    }

    private void loadTables() {
        this.connectionManager.withConnection(connection -> {
            List<String> tableNames = CommandBuilderHelper.getHelper(connection).getTableNames();
            tableNamesList.getItems().setAll(tableNames);
        });
    }

    private void deleteProfile() {
        Profile selectedItem = profileList.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            return;
        }

        if (confirm("Are you sure to delete this profile?")) {
            profileList.getItems().remove(selectedItem);
        }
    }

    private void createProfile() {
        Profile profile = new Profile("Unnamed");
        profileList.getItems().add(profile);
        profileList.getSelectionModel().select(profile);
    }

    private ListView<String> tableList() {
        return new ListView<>();
    }
}

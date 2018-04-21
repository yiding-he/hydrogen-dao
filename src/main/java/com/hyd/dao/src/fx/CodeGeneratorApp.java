package com.hyd.dao.src.fx;

import com.alibaba.fastjson.JSON;
import com.hyd.dao.database.ColumnInfo;
import com.hyd.dao.database.DatabaseType;
import com.hyd.dao.database.JDBCDriver;
import com.hyd.dao.database.commandbuilder.helper.CommandBuilderHelper;
import com.hyd.dao.log.Logger;
import com.hyd.dao.src.RepoMethodDef;
import com.hyd.dao.src.SelectedColumn;
import com.hyd.dao.src.code.*;
import com.hyd.dao.util.Str;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.DriverManager;
import java.util.Arrays;
import java.util.List;

import static com.hyd.dao.src.fx.Fx.*;
import static com.hyd.dao.src.fx.Fx.Expand.*;
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

    private TextArea repoCodeTextArea;

    private TextArea modelCodeTextArea;

    private TableView<RepoMethodDef> repoMethodTableView;

    private TableView<SelectedColumn> modelFieldTableView;

    ///////////////////////////////////////////////

    private Profile currentProfile;

    private String currentTableName;

    private ColumnInfo[] currentTableColumns;

    private DatabaseType databaseType;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        Scene scene = new Scene(root(), 1000, 700);

        initControls();

        primaryStage.setTitle(APP_NAME);
        primaryStage.setScene(scene);
        primaryStage.setOnShown(event -> onStageShown());
        primaryStage.show();
    }

    private void onStageShown() {
        primaryStage.setMaximized(true);
        loadProfiles();
    }

    private void initControls() {
        setListViewContent(profileList, Profile::getName);
        setListViewSelectionChanged(profileList, this::onSelectedProfileChanged);
        setListViewSelectionChanged(tableNamesList, this::onSelectedTableChanged);
    }

    private void onSelectedTableChanged(String tableName) {
        this.currentTableName = tableName;

        connectionManager.withConnection(connection -> {
            if (tableName != null) {
                CommandBuilderHelper helper = CommandBuilderHelper.getHelper(connection);
                this.currentTableColumns = helper.getColumnInfos(tableName);
            }
        });

        updateMethodTable(tableName);
        updateCode(tableName);
    }

    private void updateMethodTable(String tableName) {
        ObservableList<RepoMethodDef> repoMethodDefs = repoMethodTableView.getItems();
        repoMethodDefs.clear();

        if (tableName != null) {
            List<MethodDef> methods = buildRepoClassDef(currentTableName, currentProfile).methods;
            for (MethodDef method : methods) {
                if (method instanceof RepoMethodDef) {
                    repoMethodDefs.add((RepoMethodDef) method);
                }
            }
        }
    }

    private void updateCode(String tableName) {
        ClassDef repoClassDef = null;
        ClassDef modelClassDef = null;

        if (tableName != null) {
            Profile currentProfile = profileList.getSelectionModel().getSelectedItem();
            repoClassDef = buildRepoClassDef(tableName, currentProfile);
            modelClassDef = buildModelClassDef(tableName, currentProfile);
        }

        loadToModelCode(modelClassDef);
        loadToRepoTable(repoClassDef);
        loadToRepoCode(repoClassDef);
    }

    private ClassDef buildRepoClassDef(String tableName, Profile currentProfile) {
        ClassDef repoClass = currentProfile.getRepoClass(tableName);

        if (repoClass != null) {
            return repoClass;
        } else {
            RepoClassDefBuilder classDefBuilder =
                    new RepoClassDefBuilder(tableName, currentTableColumns, databaseType);

            repoClass = classDefBuilder.build(tableName);
            currentProfile.setRepoClass(tableName, repoClass);
        }

        return repoClass;
    }

    private ClassDef buildModelClassDef(String tableName, Profile currentProfile) {
        ClassDef modelClass = currentProfile.getModelClass(tableName);

        if (modelClass != null) {
            return modelClass;
        } else {
            ClassDefBuilder classDefBuilder =
                    new ModelClassBuilder(tableName, currentTableColumns, databaseType);

            modelClass = classDefBuilder.build(tableName);
            currentProfile.setModelClass(tableName, modelClass);
        }

        return modelClass;
    }

    private void loadToModelCode(ClassDef classDef) {
        if (classDef == null) {
            modelCodeTextArea.setText(null);
        } else {
            modelCodeTextArea.setText(classDef.toString());
        }
    }

    private void loadToRepoTable(ClassDef classDef) {

    }

    private void loadToRepoCode(ClassDef classDef) {
        if (classDef == null) {
            repoCodeTextArea.setText(null);
        } else {
            repoCodeTextArea.setText(classDef.toString());
        }
    }

    private void onSelectedProfileChanged(Profile profile) {
        if (profile == null) {
            profileForm.load(null);
        } else {
            profileForm.load(profile);
            currentProfile = profile;
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
                        vbox(LastExpand, 0, 0, tabPane(
                                tab("Model Class", vbox(NthExpand.set(-2), 0, PADDING,
                                        pane(0, PADDING),
                                        titledPane(-1, "Code Preview", vbox(FirstExpand, 0, 0, modelCodeArea())),
                                        hbox(NoExpand, 0, PADDING, button("Copy Code", this::copyModelCode))
                                )),
                                tab("Repository Class", vbox(NthExpand.set(-2), 0, PADDING,
                                        pane(0, PADDING),
                                        methodTable(),
                                        hbox(NoExpand, 0, PADDING,
                                                menuButton("Add...",
                                                        menuItem("Query One", this::addQueryOneMethod),
                                                        menuItem("Query List", this::addQueryListMethod),
                                                        menuItem("Query Count", this::addQueryMethod),
                                                        menuItem("Query Page", this::addQueryMethod),
                                                        new SeparatorMenuItem(),
                                                        menuItem("Insert One", this::addQueryMethod),
                                                        menuItem("Insert List", this::addQueryMethod),
                                                        new SeparatorMenuItem(),
                                                        menuItem("Update", this::addQueryMethod),
                                                        menuItem("Delete", this::addQueryMethod)
                                                ),
                                                button("Delete", this::deleteMethod)
                                        ),
                                        titledPane(-1, "Code Preview",
                                                vbox(FirstExpand, 0, 0, repoCodeArea())),
                                        hbox(NoExpand, 0, PADDING, button("Copy Code", this::copyRepoCode))
                                ))
                        ))
                )
        );
    }

    private void copyModelCode() {
        ClipboardContent clipboardContent = new ClipboardContent();
        clipboardContent.putString(modelCodeTextArea.getText());
        Clipboard.getSystemClipboard().setContent(clipboardContent);
    }

    private void copyRepoCode() {
        ClipboardContent clipboardContent = new ClipboardContent();
        clipboardContent.putString(repoCodeTextArea.getText());
        Clipboard.getSystemClipboard().setContent(clipboardContent);
    }

    private TextArea modelCodeArea() {
        modelCodeTextArea = new TextArea();
        modelCodeTextArea.setStyle("-fx-font-family: Consolas, monospace");
        modelCodeTextArea.setEditable(false);
        return modelCodeTextArea;
    }

    private TableView<SelectedColumn> modelFieldTable() {
        modelFieldTableView = new TableView<>();
        modelFieldTableView.setPrefHeight(150);
        modelFieldTableView.getColumns().add(new TableColumn<>("Column Name"));
        modelFieldTableView.getColumns().add(new TableColumn<>("Field Name"));
        return modelFieldTableView;
    }

    private TableView<RepoMethodDef> methodTable() {
        repoMethodTableView = new TableView<>();
        repoMethodTableView.setPrefHeight(150);
        repoMethodTableView.getColumns().add(column("Name", method -> method.name));
        repoMethodTableView.getColumns().add(column("Return", method -> method.returnType));
        repoMethodTableView.getColumns().add(column("Arguments", MethodDef::args2String));
        repoMethodTableView.getItems().addListener((ListChangeListener<? super RepoMethodDef>) c -> updateRepoCode());
        return repoMethodTableView;
    }

    private void updateRepoCode() {
        ClassDef repoClass = buildRepoClassDef(currentTableName, currentProfile);

        repoClass.methods.removeIf(m -> m instanceof RepoMethodDef);
        repoClass.methods.addAll(repoMethodTableView.getItems());

        loadToRepoCode(repoClass);
    }

    private TextArea repoCodeArea() {
        repoCodeTextArea = new TextArea();
        repoCodeTextArea.setStyle("-fx-font-family: Consolas, monospace");
        repoCodeTextArea.setEditable(false);
        return repoCodeTextArea;
    }

    private void deleteMethod() {
        RepoMethodDef selectedItem = repoMethodTableView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            repoMethodTableView.getItems().remove(selectedItem);
        }
    }

    private void addQueryMethod() {
    }

    private void addQueryOneMethod() {
        if (currentTableName == null) {
            return;
        }

        RepoMethodDef methodDef = new AddQueryOneMethodDialog(
                primaryStage, databaseType, currentTableName, currentTableColumns).show();

        if (methodDef != null) {
            repoMethodTableView.getItems().add(methodDef);
        }
    }

    private void addQueryListMethod() {
        if (currentTableName == null) {
            return;
        }

        RepoMethodDef methodDef = new AddQueryListMethodDialog(
                primaryStage, databaseType, currentTableName, currentTableColumns).show();

        if (methodDef != null) {
            repoMethodTableView.getItems().add(methodDef);
        }
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
        if (Str.isEmpty(currentProfile.getUrl())) {
            error("Profile is incomplete.");
            return;
        }

        if (!initConnectionManager(currentProfile)) {
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
            JDBCDriver driver = JDBCDriver.getDriverByUrl(selectedItem.getUrl());
            if (driver == null) {
                return false;
            }

            connectionManager = new ConnectionManager(() ->
                    DriverManager.getConnection(
                            selectedItem.getUrl(),
                            selectedItem.getUsername(),
                            selectedItem.getPassword()
                    )
            );

            connectionManager.withConnection(
                    connection -> databaseType = DatabaseType.of(connection));

        } catch (Exception e) {
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
        if (currentProfile == null) {
            return;
        }

        if (confirm("Are you sure to delete this profile?")) {
            profileList.getItems().remove(currentProfile);
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

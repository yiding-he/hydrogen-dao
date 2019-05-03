package com.hyd.dao.src.fx;

import com.alibaba.fastjson.JSON;
import com.hyd.dao.database.ColumnInfo;
import com.hyd.dao.database.DatabaseType;
import com.hyd.dao.database.JDBCDriver;
import com.hyd.dao.database.commandbuilder.helper.CommandBuilderHelper;
import com.hyd.dao.database.type.NameConverter;
import com.hyd.dao.log.Logger;
import com.hyd.dao.src.RepoMethodDef;
import com.hyd.dao.src.code.*;
import com.hyd.dao.src.code.method.InsertBeanMethodBuilder;
import com.hyd.dao.src.code.method.InsertMapMethodBuilder;
import com.hyd.dao.util.Str;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.DriverManager;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

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

    public static final String APP_NAME = "代码生成工具";

    public static final String[] NAME_CONVERTERS = {"属性大小写 <-> 字段下划线", "不转换"};

    private static Stage primaryStage;

    ///////////////////////////////////////////////

    private String profilePath = DEFAULT_PROFILE_PATH;

    private ListView<String> tableNamesList = new ListView<>();

    private ListView<Profile> profileList = new ListView<>();

    private Form<Profile> profileForm;

    private TextFormField<Profile> txtProfileName;

    private ConnectionManager connectionManager;

    private TextArea repoCodeTextArea;

    private TextArea modelCodeTextArea;

    private TableView<RepoMethodDef> repoMethodTableView;

    private CheckBox chkGenerateUnitTest;

    ///////////////////////////////////////////////

    private Profile currentProfile;

    private String currentTableName;

    private ColumnInfo[] currentTableColumns;

    private DatabaseType databaseType;

    //////////////////////////////////////////////////////////////

    private BooleanProperty updateCodeOnChange() {
        SimpleBooleanProperty property = new SimpleBooleanProperty();
        property.addListener((_ob, _old, _new) -> updateCode());
        return property;
    }

    private class BooleanExt extends ClassDefBuilderExt<Boolean> {

        public BooleanExt(Consumer<ClassDefBuilder> consumer) {
            super(updateCodeOnChange(), consumer);
        }
    }

    private ClassDefBuilderExt[] extensions = {
        new BooleanExt(builder -> {
            builder.addAnnotation("Data");
            builder.addImports("lombok.Data");
            ((ModelClassBuilder) builder).setGettersEnabled(false);
            ((ModelClassBuilder) builder).setSettersEnabled(false);
        }),
        new BooleanExt(builder -> {
            builder.addAnnotation("ToString");
            builder.addImports("lombok.ToString");
        }),
        new BooleanExt(builder -> {
            builder.addAnnotation("NoArgsConstructor");
            builder.addImports("lombok.NoArgsConstructor");
        }),
        new BooleanExt(builder -> {
            builder.addAnnotation("AllArgsConstructor");
            builder.addImports("lombok.AllArgsConstructor");
        }),
        new BooleanExt(builder -> {
            builder.addImports("com.baomidou.mybatisplus.annotation.*");

            builder.addAnnotation(
                new AnnotationDef("TableName").setProperty("\"" + builder.getTableName() + "\""));

            ((ModelClassBuilder) builder).addAfterFieldListener(
                (columnInfo, fieldDef) -> {
                    if (columnInfo.isPrimary()) {
                        fieldDef.addAnnotation("TableId")
                            .setProperty("\"" + columnInfo.getColumnName() + "\"");
                    } else {
                        fieldDef.addAnnotation("TableField")
                            .setProperty("\"" + columnInfo.getColumnName() + "\"");
                    }
                }
            );
        }),
    };

    //////////////////////////////////////////////////////////////

    @Override
    public void start(Stage primaryStage) throws Exception {
        CodeGeneratorApp.primaryStage = primaryStage;
        Scene scene = new Scene(root(), 1200, 700);

        initControls();

        primaryStage.setTitle(APP_NAME);
        primaryStage.setScene(scene);
        primaryStage.setOnShown(event -> onStageShown());
        primaryStage.show();
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    private void onStageShown() {
        checkFastJson();
        resizeWindow();
        loadProfiles();
    }

    private void checkFastJson() {
        try {
            Class.forName("com.alibaba.fastjson.JSON");
        } catch (ClassNotFoundException e) {
            error("请将 fastjson 添加到依赖关系再运行。");
            System.exit(0);
        }
    }

    private void resizeWindow() {
        Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
        double width = Math.min(1000, visualBounds.getWidth() - 40);
        double height = Math.min(700, visualBounds.getHeight() - 40);
        primaryStage.setX(visualBounds.getMinX() + (visualBounds.getWidth() - width) / 2);
        primaryStage.setY(visualBounds.getMinY() + (visualBounds.getHeight() - height) / 2);
        primaryStage.setWidth(width);
        primaryStage.setHeight(height);
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
            ClassDef repoClassDef = buildRepoClassDef(currentTableName, currentProfile);
            if (repoClassDef != null) {
                List<MethodDef> methods = repoClassDef.methods;
                for (MethodDef method : methods) {
                    if (method instanceof RepoMethodDef) {
                        repoMethodDefs.add((RepoMethodDef) method);
                    }
                }
            }
        }
    }

    private void updateCode() {
        updateCode(currentTableName);
    }

    private void updateCode(String tableName) {
        if (tableName == null) {
            return;
        }

        Profile currentProfile = profileList.getSelectionModel().getSelectedItem();
        ClassDef repoClassDef = buildRepoClassDef(tableName, currentProfile);
        ClassDef modelClassDef = buildModelClassDef(tableName, currentProfile);

        loadToModelCode(modelClassDef);
        loadToRepoTable(repoClassDef);
        loadToRepoCode(repoClassDef);
    }

    private ClassDef buildRepoClassDef(String tableName, Profile currentProfile) {
        if (tableName == null || currentProfile == null) {
            return null;
        }

        ClassDef repoClass = currentProfile.getRepoClass(tableName);
        String repoPackage = currentProfile.getRepoPackage();
        String modelPackage = currentProfile.getModelPackage();
        NameConverter nameConverter = getNameConverter(currentProfile);

        if (repoClass != null) {
            return repoClass;
        } else {
            RepoClassDefBuilder classDefBuilder = new RepoClassDefBuilder(
                repoPackage, modelPackage, tableName, currentTableColumns, databaseType, nameConverter
            );

            repoClass = classDefBuilder.build(tableName);
            currentProfile.setRepoClass(tableName, repoClass);
        }

        return repoClass;
    }

    private NameConverter getNameConverter(Profile currentProfile) {
        if (currentProfile.getNameConverter().equals(NAME_CONVERTERS[0])) {
            return NameConverter.CAMEL_UNDERSCORE;
        } else {
            return NameConverter.NONE;
        }
    }

    private ClassDef buildModelClassDef(String tableName, Profile currentProfile) {
        if (tableName == null || currentProfile == null) {
            return null;
        }

        String modelPackage = currentProfile.getModelPackage();
        NameConverter nameConverter = getNameConverter(currentProfile);

        ClassDefBuilder classDefBuilder = new ModelClassBuilder(
            modelPackage, tableName, currentTableColumns, databaseType, nameConverter
        );

        for (ClassDefBuilderExt<?> extension : extensions) {
            if (extension.property() instanceof BooleanProperty) {
                if (((BooleanProperty) extension.property()).get()) {
                    extension.consumer().accept(classDefBuilder);
                }
            }
        }

        ClassDef modelClass = classDefBuilder.buildClassDef(tableName);
        currentProfile.setModelClass(tableName, modelClass);

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

            primaryStage.setTitle(APP_NAME + " - " + profilePath);
        } catch (Exception e) {
            LOG.error("读取配置文件错误", e);
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
            LOG.error("保存配置文件失败", e);
            error(e);
        }
    }

    @SuppressWarnings("unchecked")
    private Parent root() {

        txtProfileName = textField("名称:", Profile::nameProperty);
        txtProfileName.setOnTextChanged(text -> profileList.refresh());

        profileForm = Fx.form(75, Arrays.asList(
            txtProfileName,
            textField("URL:", Profile::urlProperty),
            textField("用户名:", Profile::usernameProperty),
            textField("密码:", Profile::passwordProperty),
            directoryField("源码根目录:", Profile::codeRootDirProperty),
            directoryField("测试根目录:", Profile::testRootDirProperty),
            textField("Model 包名:", Profile::modelPackageProperty),
            textField("Repo 包名:", Profile::repoPackageProperty),
            comboField("命名转换", Profile::nameConverterProperty, NAME_CONVERTERS)
        ));

        Button deleteButton = button("删除", this::deleteProfile);
        Button connectButton = button("连接", this::connectProfile);

        BooleanBinding selectedProfileIsNull =
            Bindings.isNull(profileList.getSelectionModel().selectedItemProperty());

        profileForm.disableProperty().bind(selectedProfileIsNull);
        deleteButton.disableProperty().bind(selectedProfileIsNull);
        connectButton.disableProperty().bind(selectedProfileIsNull);

        chkGenerateUnitTest = new CheckBox("生成单元测试");

        return vbox(LastExpand, 0, 0,
            new MenuBar(new Menu("文件(_F)", null,
                menuItem("打开(_O)...", "Shortcut+O", this::openFile),
                menuItem("保存(_S)", "Shortcut+S", this::saveFile),
                new SeparatorMenuItem(),
                menuItem("退出(_X)", this::exit)
            )),
            hbox(LastExpand, PADDING, PADDING,
                vbox(FirstExpand, 0, PADDING,
                    titledPane(-1, "档案列表",
                        vbox(FirstExpand, PADDING, PADDING,
                            profileList,
                            hbox(Expand.NoExpand, 0, PADDING,
                                button("创建档案", this::createProfile),
                                deleteButton,
                                new Pane(),
                                connectButton
                            )
                        )),
                    titledPane(350, "档案选项", profileForm)
                ),
                vbox(LastExpand, 0, 0,
                    titledPane(-1, "数据库表",
                        vbox(LastExpand, 0, 0, tableNamesList))
                ),
                vbox(LastExpand, 0, 0, tabPane(
                    tab("Model 类", vbox(NthExpand.set(-2), 0, PADDING,
                        tabPane(
                            tab("lombok", vbox(NoExpand, PADDING, PADDING,
                                checkBox("@Data", extensions[0].property()),
                                checkBox("@ToString", extensions[1].property()),
                                checkBox("@NoArgsConstructor", extensions[2].property()),
                                checkBox("@AllArgsConstructor", extensions[3].property())
                            )),
                            tab("mybatis-plus", vbox(NoExpand, PADDING, PADDING,
                                checkBox("添加注解", extensions[4].property())
                            ))
                        ),
                        titledPane(-1, "代码预览", vbox(FirstExpand, 0, 0, modelCodeArea())),
                        hbox(NoExpand, 0, PADDING,
                            button("复制代码", this::copyModelCode),
                            button("写入项目", this::writeModelCode)
                        )
                    )),
                    tab("Repository 类", vbox(NthExpand.set(-2), 0, PADDING,
                        pane(0, PADDING),
                        methodTable(),
                        hbox(NoExpand, 0, PADDING,
                            menuButton("添加方法",
                                menuItem("查询单条记录", this::addQueryOneMethod),
                                menuItem("查询多条记录", this::addQueryListMethod),
                                menuItem("查询计数", null),
                                menuItem("分页查询", this::addPageQueryMethod),
                                new SeparatorMenuItem(),
                                menuItem("插入实体对象", this::addInsertBeanMethod),
                                menuItem("插入 Map 对象", this::addInsertMapMethod),
                                menuItem("批量插入记录", null),
                                new SeparatorMenuItem(),
                                menuItem("更新记录", null),
                                menuItem("删除记录", this::addDeleteMethod)
                            ),
                            button("删除方法", this::deleteMethod)
                        ),
                        titledPane(-1, "代码预览",
                            vbox(FirstExpand, 0, 0, repoCodeArea())),
                        hbox(NoExpand, Pos.BASELINE_LEFT, 0, PADDING,
                            button("复制代码", this::copyRepoCode),
                            button("写入项目", this::writeRepoCode),
                            chkGenerateUnitTest
                        )
                    ))
                ))
            )
        );
    }

    private void writeRepoCode() {
        try {
            if (currentTableName == null) {
                error("没有选择表");
                return;
            }

            if (currentProfile == null) {
                error("没有连接数据库");
                return;
            }

            ////////////////////////////////////////////////////////////// Repository Class

            ClassDef classDef = buildRepoClassDef(currentTableName, currentProfile);
            if (classDef.packageDef == null) {
                error("没有指定 Repository 包名");
                return;
            }

            CodeWriter.writeClass(classDef, currentProfile.getCodeRootDir());

            ////////////////////////////////////////////////////////////// Unit Test Class

            if (!chkGenerateUnitTest.isSelected()) {
                return;
            }

            ClassDef unitClassDef = buildRepoUnitTestClassDef(classDef);
            CodeWriter.writeUnitTestClass(unitClassDef, currentProfile.getTestRootDir());

        } catch (IOException e) {
            LOG.error("写入文件失败", e);
            error(e);
        }
    }

    private ClassDef buildRepoUnitTestClassDef(ClassDef repoClassDef) {
        ClassDef classDef = new ClassDef();
        classDef.imports = new ImportDef(
            "org.junit.runner.RunWith",
            "org.springframework.test.context.junit4.SpringRunner",
            "org.springframework.boot.test.context.SpringBootTest",
            "org.springframework.beans.factory.annotation.Autowired",
            "org.junit.Test"
        );
        classDef.packageDef = repoClassDef.packageDef;
        classDef.className = repoClassDef.className + "Test";
        classDef.addAnnotation(new AnnotationDef("RunWith").setProperty("SpringRunner.class"));
        classDef.addAnnotation(new AnnotationDef("SpringBootTest"));

        FieldDef repoField = new FieldDef();
        repoField.addAnnotation("Autowired");
        repoField.access = AccessType.Private;
        repoField.type = repoClassDef.className;
        repoField.name = Str.uncapitalize(repoClassDef.className);
        classDef.addFieldIfNotExists(repoField);

        return classDef;
    }

    private void writeModelCode() {
        try {
            if (currentTableName == null) {
                error("没有选择表");
                return;
            }

            if (currentProfile == null) {
                error("没有连接数据库");
                return;
            }

            ClassDef classDef = buildModelClassDef(currentTableName, currentProfile);
            if (classDef.packageDef == null) {
                error("没有指定 Model 包名");
                return;
            }

            CodeWriter.writeClass(classDef, currentProfile.getCodeRootDir());
        } catch (IOException e) {
            LOG.error("写入文件失败", e);
            error(e);
        }
    }

    private void addDeleteMethod() {
        if (currentTableName == null) {
            return;
        }

        RepoMethodDef methodDef = new AddDeleteMethodDialog(
            primaryStage, databaseType, currentTableName, currentTableColumns).show();

        if (methodDef != null) {
            repoMethodTableView.getItems().add(methodDef);
        }
    }

    private void addPageQueryMethod() {
        if (currentTableName == null) {
            return;
        }

        RepoMethodDef methodDef = new AddQueryPageMethodDialog(
            primaryStage, databaseType, currentTableName, currentTableColumns).show();

        if (methodDef != null) {
            repoMethodTableView.getItems().add(methodDef);
        }
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

    private void addInsertBeanMethod() {
        RepoMethodDef methodDef = new InsertBeanMethodBuilder(
            currentTableName, databaseType, null, null).build();

        if (methodDef != null) {
            repoMethodTableView.getItems().add(methodDef);
        }
    }

    private void addInsertMapMethod() {
        RepoMethodDef methodDef = new InsertMapMethodBuilder(
            currentTableName, databaseType, null, null).build();

        if (methodDef != null) {
            repoMethodTableView.getItems().add(methodDef);
        }
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

    private TableView<RepoMethodDef> methodTable() {
        repoMethodTableView = new TableView<>();
        repoMethodTableView.setPrefHeight(150);
        repoMethodTableView.getColumns().add(column("方法名", method -> method.name));
        repoMethodTableView.getColumns().add(column("返回类型",
            method -> method.returnType == null ? "" : method.returnType.getName()));
        repoMethodTableView.getColumns().add(column("参数", MethodDef::args2String));
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

    private void exit() {
        primaryStage.close();
    }

    private void saveFile() {
        saveProfiles();
    }

    private void openFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("."));
        File selectedFile = fileChooser.showOpenDialog(primaryStage);

        if (selectedFile != null) {
            this.profilePath = selectedFile.getAbsolutePath();
            loadProfiles();
        }
    }

    private void connectProfile() {
        if (Str.isEmpty(currentProfile.getUrl())) {
            error("档案配置不完整，请填写 URL");
            return;
        }

        if (!initConnectionManager(currentProfile)) {
            return;
        }

        currentProfile.clearModelClasses();
        currentProfile.clearRepositoryClasses();
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

        if (confirm("确定要删除该档案吗？")) {
            profileList.getItems().remove(currentProfile);
        }
    }

    private void createProfile() {
        Profile profile = new Profile("未命名");
        profileList.getItems().add(profile);
        profileList.getSelectionModel().select(profile);
    }

}

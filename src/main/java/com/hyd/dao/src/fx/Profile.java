package com.hyd.dao.src.fx;

import com.hyd.dao.src.code.ClassDef;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.HashMap;
import java.util.Map;

/**
 * (description)
 * created at 2018/4/8
 *
 * @author yidin
 */
public class Profile {

    private StringProperty name = new SimpleStringProperty();

    private StringProperty url = new SimpleStringProperty();

    private StringProperty codeRootDir = new SimpleStringProperty();

    private StringProperty testRootDir = new SimpleStringProperty();

    private StringProperty modelPackage = new SimpleStringProperty();

    private StringProperty repoPackage = new SimpleStringProperty();

    private StringProperty username = new SimpleStringProperty();

    private StringProperty password = new SimpleStringProperty();

    private StringProperty database = new SimpleStringProperty();

    private ObjectProperty<String> nameConverter = new SimpleObjectProperty<>();

    private Map<String, ClassDef> repoClassMap = new HashMap<>();

    private Map<String, ClassDef> modelClassMap = new HashMap<>();

    public Profile() {
    }

    public Profile(String name) {
        this.setName(name);
    }

    public String getNameConverter() {
        return nameConverter.get();
    }

    public ObjectProperty<String> nameConverterProperty() {
        return nameConverter;
    }

    public void setNameConverter(String nameConverter) {
        this.nameConverter.set(nameConverter);
    }

    public String getTestRootDir() {
        return testRootDir.get();
    }

    public StringProperty testRootDirProperty() {
        return testRootDir;
    }

    public void setTestRootDir(String testRootDir) {
        this.testRootDir.set(testRootDir);
    }

    public String getCodeRootDir() {
        return codeRootDir.get();
    }

    public StringProperty codeRootDirProperty() {
        return codeRootDir;
    }

    public void setCodeRootDir(String codeRootDir) {
        this.codeRootDir.set(codeRootDir);
    }

    public String getRepoPackage() {
        return repoPackage.get();
    }

    public StringProperty repoPackageProperty() {
        return repoPackage;
    }

    public void setRepoPackage(String repoPackage) {
        this.repoPackage.set(repoPackage);
    }

    public Map<String, ClassDef> getRepoClassMap() {
        return repoClassMap;
    }

    public void setRepoClassMap(Map<String, ClassDef> repoClassMap) {
        this.repoClassMap = repoClassMap;
    }

    public String getModelPackage() {
        return modelPackage.get();
    }

    public StringProperty modelPackageProperty() {
        return modelPackage;
    }

    public void setModelPackage(String modelPackage) {
        this.modelPackage.set(modelPackage);
    }

    public String getDatabase() {
        return database.get();
    }

    public StringProperty databaseProperty() {
        return database;
    }

    public void setDatabase(String database) {
        this.database.set(database);
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getUrl() {
        return url.get();
    }

    public StringProperty urlProperty() {
        return url;
    }

    public void setUrl(String url) {
        this.url.set(url);
    }

    public String getUsername() {
        return username.get();
    }

    public StringProperty usernameProperty() {
        return username;
    }

    public void setUsername(String username) {
        this.username.set(username);
    }

    public String getPassword() {
        return password.get();
    }

    public StringProperty passwordProperty() {
        return password;
    }

    public void setPassword(String password) {
        this.password.set(password);
    }

    //////////////////////////////////////////////////////////////

    public ClassDef getModelClass(String tableName) {
        return this.modelClassMap.get(tableName);
    }

    public ClassDef getRepoClass(String tableName) {
        return this.repoClassMap.get(tableName);
    }

    public void setRepoClass(String tableName, ClassDef classDef) {
        this.repoClassMap.put(tableName, classDef);
    }

    public void setModelClass(String tableName, ClassDef classDef) {
        this.modelClassMap.put(tableName, classDef);
    }

    public void clearModelClasses() {
        this.modelClassMap.clear();
    }

    public void clearRepositoryClasses() {
        this.repoClassMap.clear();
    }
}

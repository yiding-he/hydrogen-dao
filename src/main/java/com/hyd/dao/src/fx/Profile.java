package com.hyd.dao.src.fx;

import com.hyd.dao.src.AccessType;
import com.hyd.dao.src.AnnotationDef;
import com.hyd.dao.src.ClassDef;
import com.hyd.dao.src.FieldDef;
import com.hyd.dao.util.Str;
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

    private StringProperty driver = new SimpleStringProperty();

    private StringProperty username = new SimpleStringProperty();

    private StringProperty password = new SimpleStringProperty();

    private StringProperty database = new SimpleStringProperty();

    private Map<String, ClassDef> repoClassMap = new HashMap<>();

    private Map<String, ClassDef> modelClassMap = new HashMap<>();

    public Profile() {
    }

    public Profile(String name) {
        this.setName(name);
    }

    public Map<String, ClassDef> getRepoClassMap() {
        return repoClassMap;
    }

    public void setRepoClassMap(Map<String, ClassDef> repoClassMap) {
        this.repoClassMap = repoClassMap;
    }

    public String getDriver() {
        return driver.get();
    }

    public StringProperty driverProperty() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver.set(driver);
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

    public ClassDef repoClass(String tableName) {
        if (this.repoClassMap.containsKey(tableName)) {
            return this.repoClassMap.get(tableName);
        } else {
            ClassDef classDef = new ClassDef();
            classDef.className = Str.underscore2Class(tableName);

            FieldDef daoField = new FieldDef();
            daoField.access = AccessType.Private;
            daoField.name = "dao";
            daoField.type = "DAO";
            daoField.annotation = new AnnotationDef("Autowired");
            classDef.addFieldIfNotExists(daoField);

            this.repoClassMap.put(tableName, classDef);
            return classDef;
        }
    }

    public ClassDef modelClass(String tableName) {
        if (this.modelClassMap.containsKey(tableName)) {
            return this.modelClassMap.get(tableName);
        } else {
            ClassDef classDef = new ClassDef();
            classDef.className = Str.underscore2Class(tableName);
            this.modelClassMap.put(tableName, classDef);
            return classDef;
        }
    }
}

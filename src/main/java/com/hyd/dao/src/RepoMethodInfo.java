package com.hyd.dao.src;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * (description)
 * created at 2018/4/12
 *
 * @author yidin
 */
public class RepoMethodInfo {

    private ObjectProperty<RepoMethodType> type = new SimpleObjectProperty<>();

    public RepoMethodType getType() {
        return type.get();
    }

    public ObjectProperty<RepoMethodType> typeProperty() {
        return type;
    }

    public void setType(RepoMethodType type) {
        this.type.set(type);
    }
}

package com.hyd.dao.mate.ui.result;

public class PojoResultFrame extends PojoResultLayout {

    public PojoResultFrame(String pojoName, String pojoCode) {
        this.setTitle(pojoName + ".java");
        this.textArea.setText(pojoCode);
    }
}

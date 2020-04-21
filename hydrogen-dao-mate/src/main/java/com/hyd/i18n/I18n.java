package com.hyd.i18n;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class I18n {

    public static I18n getInstance(String resourceBundleName) {
        return new I18n(ResourceBundle.getBundle(resourceBundleName, new XMLResourceBundleControl()));
    }

    private final ResourceBundle resourceBundle;

    private I18n(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
    }

    public String getString(String key) {
        try {
            return resourceBundle.getString(key);
        } catch (MissingResourceException e) {
            return key;
        }
    }

    public String[] getStringArray(String key) {
        return resourceBundle.getStringArray(key);
    }
}

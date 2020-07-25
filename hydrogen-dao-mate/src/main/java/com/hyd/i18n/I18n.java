package com.hyd.i18n;

import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public final class I18n {

    private static final Map<String, I18n> MAPPINGS = new HashMap<>();

    private final ResourceBundle resourceBundle;

    public static I18n getInstance(String resourceBundleName) {
        return MAPPINGS.computeIfAbsent(resourceBundleName, key ->
            new I18n(ResourceBundle.getBundle(key, new XMLResourceBundleControl()))
        );
    }

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

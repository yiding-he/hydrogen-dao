package com.hyd.dao.config;

import org.apache.commons.lang.StringUtils;

import java.util.HashMap;

/**
 * 表示一个数据库配置
 */
public class Connection extends HashMap<String, String> {

    public String getDsName() {
        return get("dsName");
    }

    public void setDsName(String dsName) {
        put("dsName", dsName);
    }

    public boolean isDefaultConfig() {
        return get("defaultConfig") != null && get("defaultConfig").equals("true");
    }

    public void setDefaultConfig(boolean defaultConfig) {
        put("defaultConfig", defaultConfig ? "true" : "false");
    }

    public String getName() {
        return get("name");
    }

    public void setName(String name) {
        put("name", name);
    }

    public String getType() {
        return get("type");
    }

    public void setType(String type) {
        put("type", type);
    }

    public String get(String key) {
        return super.get(key);
    }

    public String get(String key, String defaultValue) {
        String value = get(key);
        return StringUtils.isBlank(value) ? defaultValue : value;
    }

    public String put(String key, String value) {
        return super.put(key, value);
    }

    public int getInt(String key) {
        return Integer.parseInt(get(key));
    }

    public boolean getBool(String key) {
        return get(key).equalsIgnoreCase("true") || get(key).equalsIgnoreCase("yes");
    }
}

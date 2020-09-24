package com.hyd.dao.mate.util;

public class Cls {

    public static boolean exists(String typeName) {
        return getType(typeName) != null;
    }

    public static Class<?> getType(String typeName) {
        try {
            return Class.forName(typeName);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }
}

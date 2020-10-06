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

    public static boolean hasField(Class<?> type, String field) {
        Class<?> _type = type;
        while (_type != null) {
            try {
                type.getDeclaredField(field);
                return true;
            } catch (NoSuchFieldException e) {
                _type = _type.getSuperclass();
            }
        }
        return false;
    }
}

package com.hyd.dao.util;

public class Cls {

    public static boolean exists(String typeName) {
        try {
            Class.forName(typeName);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}

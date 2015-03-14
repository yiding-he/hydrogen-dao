package com.hyd.dao.util;

import com.hyd.dao.DAOException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * (description)
 * created at 2015/3/14
 *
 * @author Yiding
 */
public class StaticMethodExecutor {

    public static void executeVoidNoArg(String className, String methodName) {
        try {
            Class<?> type = Class.forName(className);
            Method method = type.getMethod(methodName);
            method.invoke(null);
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }
}

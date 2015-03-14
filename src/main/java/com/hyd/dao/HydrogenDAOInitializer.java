package com.hyd.dao;

import com.hyd.dao.util.StaticMethodExecutor;

/**
 * (description)
 * created at 2015/3/14
 *
 * @author Yiding
 */
public class HydrogenDAOInitializer {

    //CHECKSTYLE:OFF
    public static void init() {
        try {
            Class.forName("ch.qos.logback.classic.PatternLayout");
            StaticMethodExecutor.executeVoidNoArg("com.hyd.dao.util.LogbackFixer", "fix");
        } catch (ClassNotFoundException e) {
            // nothing to do
        }
    }
    //CHECKSTYLE:ON
}

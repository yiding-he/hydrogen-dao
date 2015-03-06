package com.hyd.dao.util;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.pattern.LineOfCallerConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

/**
 * (description)
 * created at 2014/12/25
 *
 * @author Yiding
 */
public class LogbackFixer {

    public static boolean fixed = false;

    public static void fix() {
        if (fixed) {
            return;
        }

        PatternLayout.defaultConverterMap.put("L", MyLineOfCallerConverter.class.getName());
        PatternLayout.defaultConverterMap.put("line", MyLineOfCallerConverter.class.getName());
        fixed = true;
    }

    ////////////////////////////////////////////////////////////////

    public static class MyLineOfCallerConverter extends LineOfCallerConverter {

        @Override
        public String convert(ILoggingEvent le) {

            // 来自内部的日志取实际的行数
            if (le.getLoggerName().startsWith("com.hyd.dao.")) {
                return super.convert(le);
            }

            // 来自外部的日志，看调用堆栈中是否包含本库的调用，如果有则取堆栈中外部类的行数
            // 仅当堆栈中使用了 DefaultExecutor 时才会这么做
            StackTraceElement[] traceElements = le.getCallerData();
            boolean daostarted = false;
            boolean usingExecutor = false;

            for (StackTraceElement traceElement : traceElements) {
                String className = traceElement.getClassName();
                if (className.equals("com.hyd.dao.database.executor.DefaultExecutor")) {
                    usingExecutor = true;
                }

                if (!daostarted) {
                    if (className.startsWith("com.hyd.dao.")) {
                        daostarted = true;
                    }
                } else {
                    if (!className.startsWith("com.hyd.dao.") && usingExecutor) {
                        return Integer.toString(traceElement.getLineNumber());
                    }
                }
            }

            return super.convert(le);
        }
    }
}

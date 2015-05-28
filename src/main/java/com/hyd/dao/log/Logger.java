package com.hyd.dao.log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 支持 logback, log4j 和 log4j2 的日志框架
 * 使用方法：
 * <pre>
 *     Logger LOG = Logger.getLogger(this.getClass());
 *     LOG.debug("Hello!");
 * </pre>
 * 本类会自动检测是否存在三个日志框架之一，如果检测到则使用该框架来记录日志。
 * 如果有多个框架同时存在，则需要先指定使用哪一个。以 logback 为例，方法是：
 * <pre>
 *     Logger.setLoggerFactory(Logger.LOGBACK_FACTORY);
 * </pre>
 *
 * @author Yiding
 */
public class Logger {

    public static interface LoggerFactory {

        Object getLogger(String loggerName);

        LoggerType getLoggerType();
    }

    public static final LoggerFactory LOGBACK_FACTORY = new LoggerFactory() {

        public Object getLogger(String loggerName) {
            return executeMethod(null, "org.slf4j.LoggerFactory", "getLogger",
                    new Object[]{loggerName}, new Class[]{String.class});
        }

        public LoggerType getLoggerType() {
            return LoggerType.LOGBACK;
        }
    };

    public static final LoggerFactory LOG4J_FACTORY = new LoggerFactory() {

        public Object getLogger(String loggerName) {
            return executeMethod(null, "org.apache.log4j.Logger", "getLogger",
                    new Object[]{loggerName}, new Class[]{String.class});
        }

        public LoggerType getLoggerType() {
            return LoggerType.LOG4J;
        }
    };

    public static final LoggerFactory LOG4J2_FACTORY = new LoggerFactory() {

        public Object getLogger(String loggerName) {
            return executeMethod(null, "org.apache.logging.log4j.LogManager", "getLogger",
                    new Object[]{loggerName}, new Class[]{String.class});
        }

        public LoggerType getLoggerType() {
            return LoggerType.LOG4J2;
        }
    };

    public static final LoggerFactory JDK_FACTORY = new LoggerFactory() {

        public Object getLogger(String loggerName) {
            return java.util.logging.Logger.getLogger(loggerName);
        }

        public LoggerType getLoggerType() {
            return LoggerType.JDK;
        }
    };

    //////////////////////////////////////////////////////////////// reflection methods

    private static Class<?> cls(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object executeMethod(
            Object obj, String className, String methodName, Object[] args, Class[] types) {
        try {
            Class<?> type = Class.forName(className);
            Method method = type.getMethod(methodName, types);
            return method.invoke(obj, args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Object member(Object obj, String className, String memberName) {
        try {
            Class<?> type = Class.forName(className);
            Field field = type.getField(memberName);
            return field.get(obj);
        } catch (Exception e) {
            return null;
        }
    }

    private static Object create(String className, Object[] args, Class[] types) {
        try {
            Class<?> type = Class.forName(className);
            return type.getConstructor(types).newInstance(args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    ////////////////////////////////////////////////////////////////

    public static enum Level {
        Trace, Debug, Info, Warn, Error,;
    }

    public static enum LoggerType {
        LOGBACK("ch.qos.logback.classic.Logger", new Object[]{0, 10, 20, 30, 40}),
        LOG4J(
                "org.apache.log4j.Logger", new Object[]{
                member(null, "org.apache.log4j.Level", "TRACE"),
                member(null, "org.apache.log4j.Level", "DEBUG"),
                member(null, "org.apache.log4j.Level", "INFO"),
                member(null, "org.apache.log4j.Level", "WARN"),
                member(null, "org.apache.log4j.Level", "ERROR")
        }),
        LOG4J2("org.apache.logging.log4j.core.Logger", new Object[]{
                member(null, "org.apache.logging.log4j.Level", "TRACE"),
                member(null, "org.apache.logging.log4j.Level", "DEBUG"),
                member(null, "org.apache.logging.log4j.Level", "INFO"),
                member(null, "org.apache.logging.log4j.Level", "WARN"),
                member(null, "org.apache.logging.log4j.Level", "ERROR")
        }),
        JDK("java.util.logging.Logger", new Object[]{
                java.util.logging.Level.FINER,
                java.util.logging.Level.FINE,
                java.util.logging.Level.INFO,
                java.util.logging.Level.WARNING,
                java.util.logging.Level.SEVERE
        }),;

        public final String checkClass;

        public final Object trace;

        public final Object debug;

        public final Object info;

        public final Object warn;

        public final Object error;

        private boolean enabled;

        LoggerType(String checkClass, Object[] levelObjects) {
            this.checkClass = checkClass;
            this.trace = levelObjects[0];
            this.debug = levelObjects[1];
            this.info = levelObjects[2];
            this.warn = levelObjects[3];
            this.error = levelObjects[4];

            try {
                Class.forName(checkClass);
                this.enabled = true;
            } catch (ClassNotFoundException e) {
                this.enabled = false;
            }
        }

        public boolean enabled() {
            return enabled;
        }

        public Object levelObj(Level level) {
            if (level == Level.Trace) {
                return trace;
            } else if (level == Level.Debug) {
                return debug;
            } else if (level == Level.Info) {
                return info;
            } else if (level == Level.Warn) {
                return warn;
            } else if (level == Level.Error) {
                return error;
            }

            return null;
        }
    }

    ////////////////////////////////////////////////////////////////

    private static LoggerFactory loggerFactory;

    public static void setLoggerFactory(LoggerFactory loggerFactory) {
        Logger.loggerFactory = loggerFactory;
    }

    public static Logger getLogger(String loggerName) {

        if (loggerFactory == null) {
            autoDetect();
        }

        if (loggerFactory == null) {
            throw new RuntimeException("没有找到支持的日志框架(logback/log4j/log4j2)");
        }

        Logger l = new Logger();
        l.logger = loggerFactory.getLogger(loggerName);
        l.type = loggerFactory.getLoggerType();
        return l;
    }

    private synchronized static void autoDetect() {
        for (LoggerType loggerType : LoggerType.values()) {
            if (loggerType.enabled()) {
                if (loggerType == LoggerType.LOGBACK) {
                    loggerFactory = LOGBACK_FACTORY;
                } else if (loggerType == LoggerType.LOG4J) {
                    loggerFactory = LOG4J_FACTORY;
                } else if (loggerType == LoggerType.LOG4J2) {
                    loggerFactory = LOG4J2_FACTORY;
                } else if (loggerType == LoggerType.JDK) {
                    loggerFactory = JDK_FACTORY;
                }
                return;
            }
        }
    }

    public static Logger getLogger(Class<?> type) {
        return getLogger(type.getName());
    }

    private static final Object[] EMPTY_ARR = new Object[]{};

    ////////////////////////////////////////////////////////////////

    private Object logger;

    private LoggerType type;

    public Object getLogger() {
        return logger;
    }

    private void logLogback(Object message, Object level, Throwable throwable) {
        if (logger == null) {
            return;
        }

        executeMethod(logger, "org.slf4j.spi.LocationAwareLogger", "log",
                new Object[]{null, Logger.class.getName(), level, message, EMPTY_ARR, throwable},
                new Class[]{cls("org.slf4j.Marker"), String.class, Integer.TYPE, String.class,
                        EMPTY_ARR.getClass(), Throwable.class});
    }

    private void logLog4j(Object message, Object level, Throwable throwable) {
        if (logger == null) {
            return;
        }

        executeMethod(logger, "org.apache.log4j.Category", "log",
                new Object[]{Logger.class.getName(), level, message, throwable},
                new Class[]{String.class, cls("org.apache.log4j.Priority"), Object.class, Throwable.class});
    }

    private void logLog4j2(Object message, Object level, Throwable throwable) {
        if (logger == null) {
            return;
        }

        Class<?> levelClass = cls("org.apache.logging.log4j.Level");
        Class<?> messageClass = cls("org.apache.logging.log4j.message.Message");
        Class<?> markerClass = cls("org.apache.logging.log4j.Marker");
        Object msg = create("org.apache.logging.log4j.message.SimpleMessage",
                new Object[]{message}, new Class[]{String.class});

        executeMethod(logger, "org.apache.logging.log4j.core.Logger", "logMessage",
                new Object[]{Logger.class.getName(), level, null, msg, throwable},
                new Class[]{String.class, levelClass, markerClass, messageClass, Throwable.class});
    }

    private void logJdk(Object message, Object level, Throwable throwable) {
        if (logger == null) {
            return;
        }

        java.util.logging.Logger l = (java.util.logging.Logger) logger;
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

        // 在堆栈中向下找，找到退出本 Logger 类的地方
        int pointer = 0;
        while (pointer < stackTrace.length && !stackTrace[pointer].getClassName().equals(this.getClass().getName())) {
            pointer++;
        }
        while (pointer < stackTrace.length && stackTrace[pointer].getClassName().equals(this.getClass().getName())) {
            pointer++;
        }

        String sourceClassName = this.getClass().getName();
        String sourceMethodName = "logJDK";
        if (pointer < stackTrace.length) {
            sourceClassName = stackTrace[pointer].getClassName();
            sourceMethodName = stackTrace[pointer].getMethodName();
        }

        if (throwable != null) {
            l.logp((java.util.logging.Level) level,
                    sourceClassName, sourceMethodName, String.valueOf(message), throwable);
        } else {
            l.logp((java.util.logging.Level) level,
                    sourceClassName, sourceMethodName, String.valueOf(message));
        }
    }

    private void log(Object level, Object message, Throwable t) {
        if (type == LoggerType.LOGBACK) {
            logLogback(message, level, t);
        } else if (type == LoggerType.LOG4J) {
            logLog4j(message, level, t);
        } else if (type == LoggerType.LOG4J2) {
            logLog4j2(message, level, t);
        } else if (type == LoggerType.JDK) {
            logJdk(message, level, t);
        }
    }

    public void trace(Object message) {
        log(type.trace, message, null);
    }

    public void trace(Object message, Throwable t) {
        log(type.trace, message, t);
    }

    public void debug(Object message) {
        log(type.debug, message, null);
    }

    public void debug(Object message, Throwable t) {
        log(type.debug, message, t);
    }

    public void info(Object message) {
        log(type.info, message, null);
    }

    public void info(Object message, Throwable t) {
        log(type.info, message, t);
    }

    public void warn(Object message) {
        log(type.warn, message, null);
    }

    public void warn(Object message, Throwable t) {
        log(type.warn, message, t);
    }

    public void error(Object message) {
        log(type.error, message, null);
    }

    public void error(Object message, Throwable t) {
        log(type.error, message, t);
    }

    public boolean isEnabled(Level level) {
        String methodName = "is" + level + "Enabled";

        if (type == LoggerType.LOGBACK) {
            return (Boolean) executeMethod(logger, "org.slf4j.Logger", methodName, null, null);
        } else if (type == LoggerType.LOG4J) {
            return (Boolean) executeMethod(logger, "org.apache.log4j.Logger", methodName, null, null);
        } else if (type == LoggerType.LOG4J2) {
            return (Boolean) executeMethod(logger, "org.apache.logging.log4j.Logger", methodName, null, null);
        } else if (type == LoggerType.JDK) {
            return ((java.util.logging.Logger) logger).isLoggable((java.util.logging.Level) type.levelObj(level));
        }

        throw new IllegalStateException("Unsupported logger type '" + type + "'");
    }
}

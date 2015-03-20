package com.hyd.dao.log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
 
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
 
    ////////////////////////////////////////////////////////////////
 
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
            throw new RuntimeException(e);
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
        Trace, Debug, Info, Warn, Error
    }
 
    public static enum LoggerType {
        LOGBACK("ch.qos.logback.classic.Logger", 0, 10, 20, 30, 40),
        LOG4J("org.apache.log4j.Logger", 0, 1, 2, 3, 4),
        LOG4J2("org.apache.logging.log4j.core.Logger", 0, 1, 2, 3, 4),;
 
        public final String checkClass;
 
        public final int trace;
 
        public final int debug;
 
        public final int info;
 
        public final int warn;
 
        public final int error;
 
        private boolean enabled;
 
        LoggerType(String checkClass, int trace, int debug, int info, int warn, int error) {
            this.checkClass = checkClass;
            this.trace = trace;
            this.debug = debug;
            this.info = info;
            this.warn = warn;
            this.error = error;
 
            try {
                Class.forName(checkClass);
                this.enabled = true;
            } catch (ClassNotFoundException e) {
                this.enabled =  false;
            }
        }
 
        public boolean enabled() {
            return enabled;
        }
    }
 
    private static final Map<Integer, Object> LOG4J_LEVELS = new HashMap<Integer, Object>();
    private static final Map<Integer, Object> LOG4J2_LEVELS = new HashMap<Integer, Object>();
 
    static {
        if (LoggerType.LOG4J.enabled()) {
            LOG4J_LEVELS.put(LoggerType.LOG4J.trace, member(null, "org.apache.log4j.Level", "TRACE"));
            LOG4J_LEVELS.put(LoggerType.LOG4J.debug, member(null, "org.apache.log4j.Level", "DEBUG"));
            LOG4J_LEVELS.put(LoggerType.LOG4J.info, member(null, "org.apache.log4j.Level", "INFO"));
            LOG4J_LEVELS.put(LoggerType.LOG4J.warn, member(null, "org.apache.log4j.Level", "WARN"));
            LOG4J_LEVELS.put(LoggerType.LOG4J.error, member(null, "org.apache.log4j.Level", "ERROR"));
        }
        if (LoggerType.LOG4J2.enabled()) {
            LOG4J2_LEVELS.put(LoggerType.LOG4J2.trace, member(null, "org.apache.logging.log4j.Level", "TRACE"));
            LOG4J2_LEVELS.put(LoggerType.LOG4J2.debug, member(null, "org.apache.logging.log4j.Level", "DEBUG"));
            LOG4J2_LEVELS.put(LoggerType.LOG4J2.info, member(null,  "org.apache.logging.log4j.Level", "INFO"));
            LOG4J2_LEVELS.put(LoggerType.LOG4J2.warn, member(null,  "org.apache.logging.log4j.Level", "WARN"));
            LOG4J2_LEVELS.put(LoggerType.LOG4J2.error, member(null, "org.apache.logging.log4j.Level", "ERROR"));
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
 
    private void logLogback(Object message, int level, Throwable throwable) {
        if (logger == null) {
            return;
        }
 
        executeMethod(logger, "org.slf4j.spi.LocationAwareLogger", "log",
                new Object[]{null, Logger.class.getName(), level, message, EMPTY_ARR, throwable},
                new Class[]{cls("org.slf4j.Marker"), String.class, Integer.TYPE, String.class,
                        EMPTY_ARR.getClass(), Throwable.class});
    }
 
    private void logLog4j(Object message, int level, Throwable throwable) {
        if (logger == null) {
            return;
        }
 
        executeMethod(logger, "org.apache.log4j.Category", "log",
                new Object[]{Logger.class.getName(), LOG4J_LEVELS.get(level), message, throwable},
                new Class[]{String.class, cls("org.apache.log4j.Priority"), Object.class, Throwable.class});
    }
 
    private void logLog4j2(Object message, int level, Throwable throwable) {
        if (logger == null) {
            return;
        }
 
        Class<?> levelClass = cls("org.apache.logging.log4j.Level");
        Class<?> messageClass = cls("org.apache.logging.log4j.message.Message");
        Class<?> markerClass = cls("org.apache.logging.log4j.Marker");
        Object msg = create("org.apache.logging.log4j.message.SimpleMessage",
                new Object[]{message}, new Class[]{String.class});
 
        executeMethod(logger, "org.apache.logging.log4j.core.Logger", "logMessage",
                new Object[]{Logger.class.getName(), LOG4J2_LEVELS.get(level), null, msg, throwable},
                new Class[]{String.class, levelClass, markerClass, messageClass, Throwable.class});
    }
 
    private void log(int level, Object message, Throwable t) {
        if (type == LoggerType.LOGBACK) {
            logLogback(message, level, t);
        } else if (type == LoggerType.LOG4J) {
            logLog4j(message, level, t);
        } else if (type == LoggerType.LOG4J2) {
            logLog4j2(message, level, t);
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
        }

        throw new IllegalStateException("Unsupported logger type '" + type + "'");
    }
}
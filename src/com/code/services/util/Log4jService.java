package com.code.services.util;

import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.code.services.config.ETRConfigurationService;

public class Log4jService extends Logger {
    private final static ClassLoader cl = Log4jService.class.getClassLoader();
    private static Logger logger;

    public static void init() {
	Properties props = new Properties();
	try {
	    props.clear();
	    props.load(cl.getResourceAsStream("com/code/resources/log4j.properties"));
	    props.setProperty("log4j.appender.file.File", ETRConfigurationService.getLogFilePath());
	    PropertyConfigurator.configure(props);
	} catch (Exception e) {
	    System.out.println("Log4j : Error loading log4j.properties");
	}
    }

    public Log4jService(String name) {
	super(name);
    }

    public static void traceInfo(Class<?> cl, String msg) {
	logger = Logger.getLogger(cl);
	logger.info(msg);
    }

    public static void traceDebug(Class<?> cl, String msg) {
	logger = Logger.getLogger(cl);
	logger.debug(msg);
    }

    public static void traceError(Class<?> cl, String msg) {
	logger = Logger.getLogger(cl);
	logger.error(msg);
    }

    public static void traceFatal(Class<?> cl, String msg) {
	logger = Logger.getLogger(cl);
	logger.fatal(msg);
    }

    public static void traceErrorException(Class<?> cl, Throwable ex, String msg) {
	logger = Logger.getLogger(cl);
	logger.error(msg, ex);
    }

    public static void traceFatalException(Class<?> cl, Exception ex, String msg) {
	logger = Logger.getLogger(cl);
	logger.fatal(msg, ex);
    }

    public static void traceDebugException(Class<?> cl, Exception ex, String msg) {
	logger = Logger.getLogger(cl);
	logger.debug(msg, ex);
    }
}

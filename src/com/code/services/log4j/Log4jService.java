package com.code.services.log4j;

import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class Log4jService extends Logger {
    private final static ClassLoader cl = Log4jService.class.getClassLoader();
    static {
	Properties props = new Properties();
	try {
	    props.clear();
	    props.load(cl.getResourceAsStream("com/code/resources/log4j.properties"));
	    PropertyConfigurator.configure(props);
	} catch (Exception e) {
	    System.out.println("Log4j : Error loading log4j.properties");
	}
    }

    public Log4jService(String name) {
	super(name);
    }

    public static void traceInfo(Class<?> cl, String msg) {
	Logger logger = Logger.getLogger(cl);
	logger.info(msg);
    }

    public static void traceDebug(Class<?> cl, String msg) {
	Logger logger = Logger.getLogger(cl);
	logger.debug(msg);
    }

    public static void traceError(Class<?> cl, String msg) {
	Logger logger = Logger.getLogger(cl);
	logger.error(msg);
    }

    public static void traceFatal(Class<?> cl, String msg) {
	Logger logger = Logger.getLogger(cl);
	logger.fatal(msg);
    }

    public static void traceErrorException(Class<?> cl, Throwable ex, String msg) {
	// Log in both out and custom file
	Logger logger = Logger.getLogger(cl);
	logger.error(msg, ex);
    }

    public static void traceFatalException(Class<?> cl, Exception ex, String msg) {
	Logger logger = Logger.getLogger(cl);
	logger.fatal(msg, ex);
    }

    public static void traceDebugException(Class<?> cl, Exception ex, String msg) {
	Logger logger = Logger.getLogger(cl);
	logger.debug(msg, ex);
    }
}

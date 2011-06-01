package com.github.mnicky.bible4j;

import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.slf4j.LoggerFactory;

import com.github.mnicky.bible4j.cli.CommandParser;
import com.github.mnicky.bible4j.storage.H2DbBibleStorageFactory;

/**
 * Main class of the application, which starts the application.
 *
 */
public final class AppRunner {
    
    private final static org.slf4j.Logger logger = LoggerFactory.getLogger(AppRunner.AppLogger.class);
    
    /**
     * This class has disabled instantiating
     */
    private AppRunner() {}
    
    /**
     * Main method of the application, collecting commandline Arguments and starting the Application 
     * @param args commandline arguments
     */
    public static void main(String[] args) {

	try {
	    AppLogger.setUpLoggers(Level.OFF, new ConsoleHandler());
	    CommandParser cp = new CommandParser(new H2DbBibleStorageFactory());
	    cp.launch(args);
	} catch (Throwable e) {
	    System.out.println("Error: " + e.getMessage());
	    logger.error("Exception caught", e);
	}

    }

    /**
     * Name of this class is used as Logger name for all classes of this Application and this class is also responsible for setting the Logger level
     * and logging handler.
     *
     */
    public static class AppLogger {
	
	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(AppRunner.AppLogger.class);
	
	private static Level level;
	private static Handler handler;
	
	private static final String LOG_LEVEL_PROPERTY_NAME = "log.level";
	private static final String LOG_FILE_PROPERTY_NAME = "log.file";

	/**
	 * Sets the default log handler and log level for Logger used in bible4j.
	 * This method also checks for presence of system properties 'log.level',
	 * and 'log.file' and if exists, their value overrides default logging settings.
	 */
	public static void setUpLoggers(Level defaultLevel, Handler defaultHandler) {
	    level = defaultLevel;
	    handler = defaultHandler;
	    getLoggingProperties();
	    
	    //reset logger
	    Logger appLogger = Logger.getLogger(AppRunner.AppLogger.class.getName());
	    LogManager.getLogManager().reset();
	    
	    //set logger settings
	    appLogger.setLevel(level);
	    handler.setLevel(level);
	    appLogger.addHandler(handler);
	    LogManager.getLogManager().addLogger(appLogger);
	}

	private static void getLoggingProperties() {
	    if (System.getProperty(LOG_LEVEL_PROPERTY_NAME) != null)
		try {
		    level = Level.parse(System.getProperty(LOG_LEVEL_PROPERTY_NAME));
		} catch (Exception e) {
		    logger.warn("Exception caught when trying to set log level", e);
		}
	    
	    if (System.getProperty(LOG_FILE_PROPERTY_NAME) != null)
		try {
		    handler = new FileHandler(System.getProperty(LOG_FILE_PROPERTY_NAME));
		} catch (Exception e) {
		    logger.warn("Exception caught when trying to set log file", e);
		}
	}
    }

}

package com.github.mnicky.bible4j;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.slf4j.LoggerFactory;

import com.github.mnicky.bible4j.cli.CommandParser;
import com.github.mnicky.bible4j.storage.H2DbBibleStorageFactory;

public final class AppRunner {
    
    private final static org.slf4j.Logger logger = LoggerFactory.getLogger(AppRunner.AppLogger.class);
    
    /**
     * This class has disabled instantiating
     */
    private AppRunner() {}
    
    public static void main(String[] args) {

	try {
	    AppLogger.setUpLoggers();
	    CommandParser cp = new CommandParser(new H2DbBibleStorageFactory());
	    cp.launch(args);
	} catch (Exception e) {
	    // for user
	    System.out.println("Error: " + e.getMessage());
	    // for log
	    logger.error("Exception caught", e);
	}

    }

    // used as AppLogger for SLF4J
    public static class AppLogger {

	public static void setUpLoggers() {
	    Logger logger = Logger.getLogger(AppRunner.AppLogger.class.getName());
	    Level level = Level.OFF;
	    Logger tempLogger = logger;
	    while (tempLogger != null) {
		tempLogger.setLevel(level);
		for (Handler handler : tempLogger.getHandlers())
		    handler.setLevel(level);
		tempLogger = tempLogger.getParent();
	    }
	}
    }

}

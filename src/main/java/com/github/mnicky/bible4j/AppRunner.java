package com.github.mnicky.bible4j;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.github.mnicky.bible4j.cli.CommandParser;
import com.github.mnicky.bible4j.storage.BibleStorageException;
import com.github.mnicky.bible4j.storage.H2DbBibleStorageFactory;

public final class AppRunner {
    
    public static void main(String[] args) throws BibleStorageException {
	AppLogger.setUpLoggers();
	CommandParser cp = new CommandParser(new H2DbBibleStorageFactory());
	cp.launch(args);
	
    }

    // used as AppLogger for SLF4J
    public static class AppLogger {
	
	static void setUpLoggers() {
	    Logger logger = Logger.getLogger(AppRunner.AppLogger.class.getName());
	    Level level = Level.OFF;
	    if (logger != null) {
		logger.setLevel(level);
		for (Handler handler : logger.getHandlers())
		    handler.setLevel(level);
	    }
	}
	
    }

}

package com.github.mnicky.bible4j.storage;

import java.sql.DriverManager;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mnicky.bible4j.AppRunner;

public final class H2DbBibleStorageFactory implements BibleStorageFactory {
    
    private final static Logger logger = LoggerFactory.getLogger(AppRunner.AppLogger.class);
    
    //private String url = "jdbc:h2:tcp://localhost/test";
    private String url = "jdbc:h2:~/bible4j;MVCC=TRUE";
    
    private String user = "test";
    
    //TODO password as char array
    private String password = "";
    
    private static final String URL_PROPERTY_NAME = "h2.url";
    private static final String USER_PROPERTY_NAME = "h2.user";
    private static final String PASSWORD_PROPERTY_NAME = "h2.pwd";
    
    /**
     * The url, username and password for h2 database can be set by setter methods of this factory.
     * This factory method checks for presence of system properties 'h2.url',
     * 'h2.user' and 'h2.pwd' and if exists, their value overrides values set by setters and 
     * is used when creating the storage. If the values aren't set neither by setter methods
     * nor by the properties, the factory's default values are used.
     */
    @Override
    public BibleStorage createBibleStorage() throws BibleStorageException {
	try {
	    
	    if (System.getProperty(URL_PROPERTY_NAME) != null)
		url = System.getProperty(URL_PROPERTY_NAME);
	    
	    if (System.getProperty(USER_PROPERTY_NAME) != null)
		user = System.getProperty(USER_PROPERTY_NAME);
	    
	    if (System.getProperty(PASSWORD_PROPERTY_NAME) != null)
		password = System.getProperty(PASSWORD_PROPERTY_NAME);
	    
	    //workaround for some buggy JVMs, that don't load the driver automatically (like GCJ)
	    Class.forName("org.h2.Driver");
	    
	    return new H2DbBibleStorage(DriverManager.getConnection(url, user, password));
	
	} catch (SQLException e) {
	    logger.error("Exception caught when creating H2DbBibleStorage", e);
	    throw new BibleStorageException("BibleStorage could not be created", e);
	} catch (ClassNotFoundException e) {
	    logger.error("Exception caught when creating H2DbBibleStorage", e);
	    throw new BibleStorageException("BibleStorage could not be created", e);
	}
    }

    public void setUrl(String url) {
	this.url = url;
    }

    public void setUser(String user) {
	this.user = user;
    }

    public void setPassword(String password) {
	this.password = password;
    }

}

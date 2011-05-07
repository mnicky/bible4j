package com.github.mnicky.bible4j.storage;

import java.sql.DriverManager;
import java.sql.SQLException;

public final class H2DbBibleStorageFactory implements BibleStorageFactory {
    
    private String url = "jdbc:h2:tcp://localhost/test";
    //private String url = "jdbc:h2:~/bible4j";
    
    private String user = "test";
    
    //TODO password as char array
    private String password = "";
    
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
	    
	    if (System.getProperty("h2.url") != null)
		url = System.getProperty("h2.url");
	    
	    if (System.getProperty("h2.user") != null)
		user = System.getProperty("h2.user");
	    
	    if (System.getProperty("h2.pwd") != null)
		password = System.getProperty("h2.pwd");
	    
	    return new H2DbBibleStorage(DriverManager.getConnection(url, user, password));
	
	} catch (SQLException e) {
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

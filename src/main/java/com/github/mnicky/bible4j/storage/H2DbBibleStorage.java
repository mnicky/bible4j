package com.github.mnicky.bible4j.storage;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * {@link BibleStorage} backed by <a href="http://h2database.com">H2 database</a>.
 */
public final class H2DbBibleStorage implements BibleStorage {
    
    /**
     * Connection to H2 database.
     */
    private final Connection dbConnection;
    
    /**
     * Constructs new H2DbBibleStorage with specified connection to H2 database.
     * @param dbConnection conection to H2 database
     */
    public H2DbBibleStorage(Connection dbConnection) {
	this.dbConnection = dbConnection;
    }
    
    /**
     * Closes this H2BibleStorage.
     * @throws BibleStorageException when H2DbBibleStorage can't be closed
     */
    public void close() throws BibleStorageException {
	try {
	    this.dbConnection.close();
	} catch (SQLException e) {
	    throw new BibleStorageException("BibleStorage could not be closed", e);
	}
    }
    
}

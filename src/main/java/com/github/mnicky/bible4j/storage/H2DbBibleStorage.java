package com.github.mnicky.bible4j.storage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * {@link BibleStorage} backed by <a href="http://h2database.com">H2
 * database</a>.
 */
public final class H2DbBibleStorage implements BibleStorage {

    /**
     * Connection to H2 database.
     */
    private final Connection dbConnection;

    /**
     * Constructs new H2DbBibleStorage with specified connection to H2 database.
     * 
     * @param dbConnection
     *            conection to H2 database
     */
    public H2DbBibleStorage(Connection dbConnection) {
	this.dbConnection = dbConnection;
    }

    /**
     * Closes this H2BibleStorage.
     * 
     * @throws BibleStorageException
     *             when H2DbBibleStorage can't be closed
     */
    @Override
    public void close() throws BibleStorageException {
	try {
	    this.dbConnection.close();
	} catch (SQLException e) {
	    throw new BibleStorageException("BibleStorage could not be closed", e);
	}
    }

    @Override
    public void createStorage() throws BibleStorageException {

    }

    private int commitUpdate(PreparedStatement st) throws SQLException {

	int rows = 0;

	try {
	    st.getConnection().setAutoCommit(false);
	    rows = st.executeUpdate();
	    st.getConnection().commit();
	} catch (SQLException e) {
	    st.getConnection().rollback();
	    rows = 0;
	    e.printStackTrace(); // TODO not an abstraction leak?
	} finally {
	    st.getConnection().setAutoCommit(true);
	    st.close();
	}

	return rows;
    }

    private ResultSet commitQuery(PreparedStatement st) throws SQLException {

	ResultSet result = null;

	try {
	    st.getConnection().setAutoCommit(false);
	    result = st.executeQuery();
	    st.getConnection().commit();
	} catch (SQLException e) {
	    st.getConnection().rollback();
	    result = null;
	    e.printStackTrace(); // TODO not an abstraction leak?
	} finally {
	    st.getConnection().setAutoCommit(true);
	    st.close();
	}

	return result;
    }

}

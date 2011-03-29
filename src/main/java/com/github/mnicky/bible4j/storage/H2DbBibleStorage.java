package com.github.mnicky.bible4j.storage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

import org.testng.Assert;

import com.github.mnicky.bible4j.data.BibleBook;
import com.github.mnicky.bible4j.data.Position;
import com.github.mnicky.bible4j.data.Verse;

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

    // TODO needed?
    private int commitUpdate(PreparedStatement st) throws SQLException {

	int rows = 0;
	Connection con = st.getConnection();

	try {
	    con.setAutoCommit(false);
	    rows = st.executeUpdate();
	    con.commit();
	} catch (SQLException e) {
	    if (con != null)
		con.rollback();
	    throw e;
	} finally {
	    if (con != null)
		con.setAutoCommit(true);
	    st.close();
	}

	return rows;
    }

    // TODO needed?
    private ResultSet commitQuery(PreparedStatement st) throws SQLException {

	ResultSet result = null;
	Connection con = st.getConnection();

	try {
	    st.getConnection().setAutoCommit(false);
	    result = st.executeQuery();
	    st.getConnection().commit();
	} catch (SQLException e) {
	    if (con != null)
		st.getConnection().rollback();
	    result = null;
	    throw e;
	} finally {
	    if (con != null)
		st.getConnection().setAutoCommit(true);
	    st.close();
	}

	return result;
    }

    private int[] commitBatch(Statement st) throws SQLException {

	int[] rows = null;
	Connection con = st.getConnection();

	try {
	    con.setAutoCommit(false);
	    rows = st.executeBatch();
	    con.commit();
	} catch (SQLException e) {
	    if (con != null)
		con.rollback();
	    throw e;
	} finally {
	    if (con != null)
		con.setAutoCommit(true);
	    st.close();
	}

	return rows;
    }

    @Override
    public int[] createStorage() throws BibleStorageException {

	int[] columns;

	try {
	    Statement st = dbConnection.createStatement();

	    // FIXME add CASCADE or RESTRICTED to f. keys?
	    // TODO convert more VARCHARs to V_IGNORECASE?

	    st.addBatch("CREATE TABLE IF NOT EXISTS `bible_versions` (" + "`id` IDENTITY NOT NULL,"
		    + "`name` VARCHAR_IGNORECASE(50) NOT NULL," + "`lang` VARCHAR(50) NOT NULL)");

	    st.addBatch("CREATE TABLE IF NOT EXISTS `bible_books` (" + "`id` IDENTITY NOT NULL,"
		    + "`name` VARCHAR_IGNORECASE(50)," + "`is_deutero` BOOLEAN)");

	    st.addBatch("CREATE TABLE IF NOT EXISTS `coords` (" + "`id` IDENTITY NOT NULL,"
		    + "`bible_book_id` BIGINT NOT NULL REFERENCES bible_books(id),"
		    + "`chapter_num` INT," + "`verse_num` INT)");

	    st.addBatch("CREATE TABLE IF NOT EXISTS `verses` (" + "`id` IDENTITY NOT NULL,"
		    + "`text` VARCHAR(500) NOT NULL,"
		    + "`bible_version_id` BIGINT NOT NULL REFERENCES bible_versions(id),"
		    + "`coord_id` BIGINT NOT NULL REFERENCES coords(id))");

	    st.addBatch("CREATE TABLE IF NOT EXISTS `notes` (" + "`id` IDENTITY NOT NULL,"
		    + "`type` VARCHAR(1) NOT NULL," + "`text` VARCHAR(500) NOT NULL,"
		    + "`coord_id` BIGINT NOT NULL REFERENCES coords(id))");

	    st.addBatch("CREATE TABLE IF NOT EXISTS `bookmarks` (" + "`id` IDENTITY NOT NULL,"
		    + "`name` VARCHAR(50) NOT NULL,"
		    + "`verse_id` BIGINT NOT NULL REFERENCES verses(id))");

	    st.addBatch("CREATE TABLE IF NOT EXISTS `daily_readings_lists` ("
		    + "`id` IDENTITY NOT NULL," + "`name` VARCHAR(50) NOT NULL)");

	    st.addBatch("CREATE TABLE IF NOT EXISTS `daily_readings` ("
		    + "`id` IDENTITY NOT NULL,"
		    + "`date` DATE NOT NULL,"
		    + "`daily_readings_list_id` BIGINT NOT NULL REFERENCES daily_readings_lists(id))");

	    st.addBatch("CREATE TABLE IF NOT EXISTS `readings_coords` ("
		    + "`id` IDENTITY NOT NULL,"
		    + "`coord_id` BIGINT NOT NULL REFERENCES coords(id),"
		    + "`reading_id` BIGINT NOT NULL REFERENCES daily_readings(id))");

	    st.addBatch("CREATE TABLE IF NOT EXISTS `dict_terms` (" + "`id` IDENTITY NOT NULL,"
		    + "`name` VARCHAR(50) NOT NULL," + "`def` VARCHAR(500) NOT NULL)");

	    columns = commitBatch(st);

	} catch (SQLException e) {
	    throw new BibleStorageException("BibleStorage could not be created", e);
	}

	return columns;
    }

    @Override
    public void insertVerse(Verse verse) throws BibleStorageException {

	try {
	    PreparedStatement st = dbConnection.prepareStatement("INSERT INTO verses"
		    + "(`text`, `bible_version_id`, `coord_id`) VALUES ( ?, ?, ?)");
	    st.setString(1, verse.getText());
	    st.setLong(2, '0');// FIXME add real num
	    st.setLong(3, '0');// FIXME add real num
	    commitBatch(st);
	} catch (SQLException e) {
	    throw new BibleStorageException("Verse could not be inserted", e);
	}

    }

    @Override
    public void insertBibleBook(BibleBook book) throws BibleStorageException {

	try {
	    PreparedStatement st = dbConnection.prepareStatement("INSERT INTO bible_books"
		    + "(`name`, `is_deutero`) VALUES ( ?, ?)");
	    st.setString(1, book.getName());
	    st.setBoolean(2, book.isDeutero());
	    commitUpdate(st);
	} catch (SQLException e) {
	    throw new BibleStorageException("Bible book could not be inserted", e);
	}
    }

    @Override
    public void insertPosition(Position position) throws BibleStorageException {
	try {
	    PreparedStatement st = dbConnection
		    .prepareStatement("INSERT INTO coords"
			    + "(`bible_book_id`, `chapter_num`, `verse_num`) VALUES ((SELECT `id` FROM `bible_books` WHERE `name` = ? LIMIT 1), ?, ?)");
	    st.setString(1, position.getBook().getName());
	    st.setInt(2, position.getChapterNum());
	    st.setInt(3, position.getVerseNum());
	    commitUpdate(st);
	} catch (SQLException e) {
	    throw new BibleStorageException("Position could not be inserted", e);
	}
    }

}

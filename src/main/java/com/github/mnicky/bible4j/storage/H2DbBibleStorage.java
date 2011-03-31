package com.github.mnicky.bible4j.storage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.github.mnicky.bible4j.data.BibleBook;
import com.github.mnicky.bible4j.data.BibleVersion;
import com.github.mnicky.bible4j.data.Position;
import com.github.mnicky.bible4j.data.Verse;

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
    // TODO use CachedRowSet instead of ResultSet ?
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

	    // FIXME add CASCADE or RESTRICTED to f. keys etc?
	    // TODO convert more VARCHARs to V_IGNORECASE?
	    // TODO add more UNIQUE constraints, CHECK etc... ?

	    st.addBatch("CREATE TABLE IF NOT EXISTS `bible_versions` ("
		    + "`id` INT IDENTITY NOT NULL,"
		    + "`name` VARCHAR_IGNORECASE(50) NOT NULL UNIQUE,"
		    + "`lang` VARCHAR(50) NOT NULL)");

	    st.addBatch("CREATE TABLE IF NOT EXISTS `bible_books` ("
		    + "`id` INT IDENTITY NOT NULL,"
		    + "`name` VARCHAR_IGNORECASE(50) NOT NULL UNIQUE,"
		    + "`is_deutero` BOOLEAN NOT NULL)");

	    st.addBatch("CREATE TABLE IF NOT EXISTS `coords` ("
		    + "`id` INT IDENTITY NOT NULL,"
		    + "`bible_book_id` INT NOT NULL,"
		    + "`chapter_num` INT NOT NULL,"
		    + "`verse_num` INT NOT NULL,"
		    + "FOREIGN KEY (`bible_book_id`) REFERENCES `bible_books`)");

	    st.addBatch("CREATE TABLE IF NOT EXISTS `verses` ("
		    + "`id` INT IDENTITY NOT NULL,"
		    + "`text` VARCHAR(500) NOT NULL,"
		    + "`bible_version_id` INT NOT NULL,"
		    + "`coord_id` INT NOT NULL,"
		    + "FOREIGN KEY (`bible_version_id`) REFERENCES `bible_versions`,"
		    + "FOREIGN KEY (`coord_id`) REFERENCES `coords`)");

	    st.addBatch("CREATE TABLE IF NOT EXISTS `notes` ("
		    + "`id` INT IDENTITY NOT NULL,"
		    + "`type` VARCHAR(1) NOT NULL,"
		    + "`text` VARCHAR(500) NOT NULL,"
		    + "`coord_id` INT NOT NULL,"
		    + "FOREIGN KEY (`coord_id`) REFERENCES `coords`)");

	    st.addBatch("CREATE TABLE IF NOT EXISTS `bookmarks` ("
		    + "`id` INT IDENTITY NOT NULL,"
		    + "`name` VARCHAR(50) NOT NULL,"
		    + "`verse_id` INT NOT NULL,"
		    + "FOREIGN KEY (`verse_id`) REFERENCES `verses`)");

	    st.addBatch("CREATE TABLE IF NOT EXISTS `daily_readings_lists` ("
		    + "`id` INT IDENTITY NOT NULL,"
		    + "`name` VARCHAR(50) NOT NULL UNIQUE)");

	    st.addBatch("CREATE TABLE IF NOT EXISTS `daily_readings` ("
		    + "`id` INT IDENTITY NOT NULL,"
		    + "`date` DATE NOT NULL,"
		    + "`daily_readings_list_id` INT NOT NULL,"
		    + "FOREIGN KEY (`daily_readings_list_id`) REFERENCES `daily_readings_lists`)");

	    st.addBatch("CREATE TABLE IF NOT EXISTS `readings_coords` ("
		    + "`id` INT IDENTITY NOT NULL,"
		    + "`coord_id` INT NOT NULL,"
		    + "`reading_id` INT NOT NULL,"
		    + "FOREIGN KEY (`coord_id`) REFERENCES `coords`,"
		    + "FOREIGN KEY (`reading_id`) REFERENCES `daily_readings`)");

	    st.addBatch("CREATE TABLE IF NOT EXISTS `dict_terms` ("
		    + "`id` INT IDENTITY NOT NULL,"
		    + "`name` VARCHAR(50) NOT NULL UNIQUE,"
		    + "`def` VARCHAR(500) NOT NULL)");

	    columns = commitBatch(st);

	} catch (SQLException e) {
	    throw new BibleStorageException("BibleStorage could not be created", e);
	}

	return columns;
    }

    @Override
    public void insertVerse(Verse verse) throws BibleStorageException {
	try {
	    PreparedStatement st = dbConnection
		    .prepareStatement("INSERT INTO verses (`text`, `bible_version_id`, `coord_id`) VALUES "
			    + "( ?,"
			    + "(SELECT DISTINCT `id` FROM `bible_versions` WHERE `name` = ?),"
			    + "(SELECT DISTINCT `id` FROM `coords` WHERE `chapter_num` = ? AND `bible_book_id` = (SELECT DISTINCT `id` FROM `bible_books` WHERE `name` = ?) AND `verse_num` = ? ))");
	    st.setString(1, verse.getText());
	    st.setString(2, verse.getBibleVersion().getName());
	    st.setInt(3, verse.getPosition().getChapterNum());
	    st.setString(4, verse.getPosition().getBook().getName());
	    st.setInt(5, verse.getPosition().getVerseNum());
	    commitUpdate(st);
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
			    + "(`bible_book_id`, `chapter_num`, `verse_num`) VALUES ((SELECT DISTINCT `id` FROM `bible_books` WHERE `name` = ?), ?, ?)");
	    st.setString(1, position.getBook().getName());
	    st.setInt(2, position.getChapterNum());
	    st.setInt(3, position.getVerseNum());
	    commitUpdate(st);
	} catch (SQLException e) {
	    throw new BibleStorageException("Position could not be inserted", e);
	}
    }

    @Override
    public void insertBibleVersion(BibleVersion version) throws BibleStorageException {
	try {
	    PreparedStatement st = dbConnection.prepareStatement("INSERT INTO bible_versions"
		    + "(`name`, `lang`) VALUES ( ?, ?)");
	    st.setString(1, version.getName());
	    st.setString(2, version.getLanguage());
	    commitUpdate(st);
	} catch (SQLException e) {
	    throw new BibleStorageException("Bible version could not be inserted", e);
	}
    }

    @Override
    public Verse getVerse(Position position, BibleVersion version) throws BibleStorageException {
	ResultSet rs = null;
	PreparedStatement st = null;
	Verse verse = null;

	try {
	    st = dbConnection
		    .prepareStatement("SELECT `text`, `bible_versions`.`name` AS version, `lang`, `verse_num`, `chapter_num`, `bible_books`.`name` AS `book` "
			    + "FROM bible_versions "
			    + "INNER JOIN `verses` ON `bible_version_id` = `bible_versions`.`id` "
			    + "INNER JOIN `coords` ON `coord_id` = `coords`.`id` "
			    + "INNER JOIN `bible_books` ON `bible_book_id` = `bible_books`.`id` "
			    + "WHERE `chapter_num` = ? AND `bible_books`.`name` = ? AND `bible_versions`.`name` = ? AND `verse_num` = ? LIMIT 1");
	    st.setInt(1, position.getChapterNum());
	    st.setString(2, position.getBook().getName());
	    st.setString(3, version.getName());
	    st.setInt(4, position.getVerseNum());
	    rs = commitQuery(st);
	    while (rs.next())
		verse = new Verse(rs.getString("text"), new Position(BibleBook.getBibleBookByName(rs
			.getString("book")), rs.getInt("chapter_num"), rs.getInt("verse_num")),
			new BibleVersion(rs.getString("version"), rs.getString("lang")));

	} catch (SQLException e) {
	    throw new BibleStorageException("Verse could not be retrieved", e);
	} finally {
	    try {
		if (rs != null)
		    rs.close();
		if (st != null)
		    st.close();
	    } catch (SQLException e) {
		e.printStackTrace();
	    }
	}
	return verse;
    }

    @Override
    public List<Verse> getVerses(List<Position> positions, BibleVersion version) throws BibleStorageException {
	ResultSet rs = null;
	PreparedStatement st = null;
	List<Verse> verseList = new ArrayList<Verse>();

	for (Position position : positions) {

	    try {
		st = dbConnection
			.prepareStatement("SELECT `text`, `bible_versions`.`name` AS version, `lang`, `verse_num`, `chapter_num`, `bible_books`.`name` AS `book` "
				+ "FROM bible_versions "
				+ "INNER JOIN `verses` ON `bible_version_id` = `bible_versions`.`id` "
				+ "INNER JOIN `coords` ON `coord_id` = `coords`.`id` "
				+ "INNER JOIN `bible_books` ON `bible_book_id` = `bible_books`.`id` "
				+ "WHERE `chapter_num` = ? AND `bible_books`.`name` = ? AND `bible_versions`.`name` = ? AND `verse_num` = ? LIMIT 1");
		st.setInt(1, position.getChapterNum());
		st.setString(2, position.getBook().getName());
		st.setString(3, version.getName());
		st.setInt(4, position.getVerseNum());
		rs = commitQuery(st);
		while (rs.next())
		    verseList.add(new Verse(rs.getString("text"), new Position(BibleBook
			    .getBibleBookByName(rs
				    .getString("book")), rs.getInt("chapter_num"), rs
			    .getInt("verse_num")), new BibleVersion(rs.getString("version"), rs
			    .getString("lang"))));

	    } catch (SQLException e) {
		throw new BibleStorageException("Verses could not be retrieved", e);
	    } finally {
		try {
		    if (st != null)
			st.close();
		} catch (SQLException e) {
		    e.printStackTrace();
		}
	    }
	}

	return verseList;
    }

    @Override
    public List<Verse> compareVerses(Position position, List<BibleVersion> versions)
	    throws BibleStorageException {
	ResultSet rs = null;
	PreparedStatement st = null;
	List<Verse> verseList = new ArrayList<Verse>();

	for (BibleVersion version : versions) {

	    try {
		st = dbConnection
			.prepareStatement("SELECT `text`, `bible_versions`.`name` AS version, `lang`, `verse_num`, `chapter_num`, `bible_books`.`name` AS `book` "
				+ "FROM bible_versions "
				+ "INNER JOIN `verses` ON `bible_version_id` = `bible_versions`.`id` "
				+ "INNER JOIN `coords` ON `coord_id` = `coords`.`id` "
				+ "INNER JOIN `bible_books` ON `bible_book_id` = `bible_books`.`id` "
				+ "WHERE `chapter_num` = ? AND `bible_books`.`name` = ? AND `bible_versions`.`name` = ? AND `verse_num` = ? LIMIT 1");
		st.setInt(1, position.getChapterNum());
		st.setString(2, position.getBook().getName());
		st.setString(3, version.getName());
		st.setInt(4, position.getVerseNum());
		rs = commitQuery(st);
		while (rs.next())
		    verseList.add(new Verse(rs.getString("text"), new Position(BibleBook
			    .getBibleBookByName(rs
				    .getString("book")), rs.getInt("chapter_num"), rs
			    .getInt("verse_num")), new BibleVersion(rs.getString("version"), rs
			    .getString("lang"))));

	    } catch (SQLException e) {
		throw new BibleStorageException("Verses could not be retrieved", e);
	    } finally {
		try {
		    if (st != null)
			st.close();
		} catch (SQLException e) {
		    e.printStackTrace();
		}
	    }
	}

	return verseList;
    }

}

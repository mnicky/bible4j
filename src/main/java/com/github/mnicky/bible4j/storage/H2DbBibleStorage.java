package com.github.mnicky.bible4j.storage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mnicky.bible4j.AppRunner;
import com.github.mnicky.bible4j.data.BibleBook;
import com.github.mnicky.bible4j.data.BibleVersion;
import com.github.mnicky.bible4j.data.Bookmark;
import com.github.mnicky.bible4j.data.Note;
import com.github.mnicky.bible4j.data.Position;
import com.github.mnicky.bible4j.data.Verse;
import static com.github.mnicky.bible4j.storage.H2DbNaming.*;

/**
 * {@link BibleStorage} backed by <a href="http://h2database.com">H2 database</a>.
 */
public final class H2DbBibleStorage implements BibleStorage {
    
    private final static Logger logger = LoggerFactory.getLogger(AppRunner.AppLogger.class);

    /**
     * Connection to H2 database.
     */
    private final Connection dbConnection;

    /**
     * Constructs new H2DbBibleStorage with specified connection to H2 database.
     * 
     * @param dbConnection conection to H2 database
     */
    public H2DbBibleStorage(Connection dbConnection) {
	this.dbConnection = dbConnection;
    }

    /**
     * Closes this H2BibleStorage.
     * 
     * @throws BibleStorageException when H2DbBibleStorage can't be closed
     */
    @Override
    public void close() {
	try {
	    this.dbConnection.close();
	} catch (SQLException e) {
	    logger.error("Exception caught when closing this BibleStorage:", this, e);
	    throw new BibleStorageException("BibleStorage could not be closed", e);
	}
    }

    // TODO needed?
    private int commitUpdate(PreparedStatement st) throws SQLException {

	int rows = 0;
	Connection con = st.getConnection();

	try {
	    con.setAutoCommit(false);
	    logger.debug("Executing SQL update: {}", st);
	    rows = st.executeUpdate();
	    con.commit();
	} catch (SQLException e) {
	    logger.error("Exception caught when committing this SQL update: {}", st, e);
	    if (con != null)
		con.rollback();
	    throw e;
	} finally {
		st.close();
	    if (con != null)
		con.setAutoCommit(true);
	}

	return rows;
    }

    // TODO needed?
    // TODO use CachedRowSet instead of ResultSet ?
    private ResultSet commitQuery(PreparedStatement st) throws SQLException {

	ResultSet result = null;
	Connection con = st.getConnection();

	try {
	    con.setAutoCommit(false);
	    logger.debug("Executing SQL query: {}", st);
	    result = st.executeQuery();
	    con.commit();
	} catch (SQLException e) {
	    logger.error("Exception caught when committing this SQL query: {}", st, e);
	    if (con != null)
		con.rollback();
	    result = null;
	    throw e;
	} finally {
	    if (con != null)
		con.setAutoCommit(true);
	}

	return result;
    }

    private int[] commitBatch(Statement st) throws SQLException {

	int[] rows = null;
	Connection con = st.getConnection();

	try {
	    con.setAutoCommit(false);
	    logger.debug("Executing SQL batch update: {}", st);
	    rows = st.executeBatch();
	    con.commit();
	} catch (SQLException e) {
	    logger.error("Exception caught when committing this SQL batch statement: {}", st, e);
	    if (con != null)
		con.rollback();
	    throw e;
	} finally {
		st.close();
	    if (con != null)
		con.setAutoCommit(true);
	    st.close();
	}

	return rows;
    }
    
    private boolean tableExists(String tableName) throws SQLException {
	ResultSet rs = null;
	PreparedStatement st = null;
	int count = 0;
	try {
	    st = dbConnection
		    .prepareStatement("SELECT COUNT(`TABLE_NAME`) FROM `INFORMATION_SCHEMA`.`TABLES` WHERE `TABLE_SCHEMA` = 'PUBLIC' AND `TABLE_NAME` = ?");
	    st.setString(1, tableName);
	    rs = commitQuery(st);
	    while (rs.next())
		count = rs.getInt(1);

	} finally {
	    if (rs != null)
		rs.close();
	    if (st != null)
		st.close();
	}
	return (count > 0);
    }
    
    public boolean isStorageInitialized() {
	try {	    
	    if (tableExists(VERSIONS_BARE) && tableExists(BOOKS_BARE) && tableExists(COORDS_BARE)
		    && tableExists(VERSES_BARE) && tableExists(NOTES_BARE) && tableExists(BKMARKS_BARE))
	        return true;
	    else
	        return false;
	} catch (SQLException e) {
	    logger.error("Exception caught when checking if the storage is initialized", e);
	    throw new BibleStorageException("Could not be checked whether the Bible storage is initialized.", e);
	}
    }

    @Override
    public int[] initializeStorage() {

	int[] columns;

	try {
	    if (isStorageInitialized())
		return null;
	    
	    Statement st = dbConnection.createStatement();

	    // FIXME add CASCADE or RESTRICTED to foreign keys etc?
	    // TODO add more UNIQUE constraints, CHECK etc... ?

	    st.addBatch("CREATE ALIAS IF NOT EXISTS FT_INIT FOR \"org.h2.fulltext.FullText.init\";CALL FT_INIT();");

	    st.addBatch("CREATE TABLE IF NOT EXISTS " + VERSIONS + " ("
		    + VERSION_ID + " INT IDENTITY NOT NULL,"
		    + VERSION_ABBR + " VARCHAR_IGNORECASE(50) NOT NULL UNIQUE,"
		    + VERSION_NAME + " VARCHAR_IGNORECASE(50) NOT NULL,"
		    + VERSION_LANG + " VARCHAR_IGNORECASE(50) NOT NULL)");

	    st.addBatch("CREATE TABLE IF NOT EXISTS " + BOOKS + " ("
		    + BOOK_ID + " INT IDENTITY NOT NULL,"
		    + BOOK_NAME + " VARCHAR_IGNORECASE(50) NOT NULL UNIQUE,"
		    + BOOK_DEUT + " BOOLEAN NOT NULL)");

	    st.addBatch("CREATE TABLE IF NOT EXISTS " + COORDS + " ("
		    + COORD_ID + " INT IDENTITY NOT NULL,"
		    + COORD_BOOK + " INT NOT NULL,"
		    + COORD_CHAPT + " INT NOT NULL,"
		    + COORD_VERSE + " INT NOT NULL,"
		    + "CONSTRAINT `coords_unique` UNIQUE ( " + COORD_BOOK + ", " + COORD_CHAPT + ", " + COORD_VERSE + ")," 
		    + "FOREIGN KEY (" + COORD_BOOK + ") REFERENCES " + BOOKS + ")");

	    st.addBatch("CREATE TABLE IF NOT EXISTS " + VERSES + " ("
		    + VERSE_ID + " INT IDENTITY NOT NULL,"
		    + VERSE_TEXT + " VARCHAR(4096) NOT NULL,"
		    + VERSE_VERSION + " INT NOT NULL,"
		    + VERSE_COORD + " INT NOT NULL,"
		    + "FOREIGN KEY (" + VERSE_VERSION + ") REFERENCES " + VERSIONS + ","
		    + "FOREIGN KEY (" + VERSE_COORD + ") REFERENCES " + COORDS + ")");

	    st.addBatch("CREATE TABLE IF NOT EXISTS " + NOTES + " ("
		    + NOTE_ID + " INT IDENTITY NOT NULL,"
		    + NOTE_TYPE + " VARCHAR_IGNORECASE(1) NOT NULL,"
		    + NOTE_TEXT + " VARCHAR(500) NOT NULL,"
		    + NOTE_COORD + " INT NOT NULL,"
		    + "FOREIGN KEY (" + NOTE_COORD + ") REFERENCES " + COORDS + ")");

	    st.addBatch("CREATE TABLE IF NOT EXISTS " + BKMARKS + " ("
		    + BKMARK_ID + " INT IDENTITY NOT NULL,"
		    + BKMARK_NAME + " VARCHAR_IGNORECASE(50) NOT NULL,"
		    + BKMARK_VERSE + " INT NOT NULL,"
		    + "FOREIGN KEY (" + BKMARK_VERSE + ") REFERENCES " + VERSES + ")");

	    // TODO make constants from strings
	    st.addBatch("CALL FT_CREATE_INDEX('PUBLIC', '" + VERSES_BARE + "', 'TEXT');");

	    columns = commitBatch(st);

	} catch (SQLException e) {
	    logger.error("Exception caught when initializing this BibleStorage: {}", this, e);
	    throw new BibleStorageException("BibleStorage could not be initialized", e);
	}

	return columns;
    }

    @Override
    public void insertVerse(Verse verse) {
	try {
	    PreparedStatement st = dbConnection
		    .prepareStatement("INSERT INTO " + VERSES + " (" + VERSE_TEXT + ", " + VERSE_VERSION
			    + ", " + VERSE_COORD + ") VALUES "
			    + "( ?,"
			    + "(SELECT DISTINCT " + VERSION_ID_F + " FROM " + VERSIONS
					+ " WHERE " + VERSION_ABBR_F + " = ?),"
			    + "(SELECT DISTINCT " + COORD_ID_F + " FROM " + COORDS
					+ " WHERE " + COORD_CHAPT_F + " = ? AND "
					+ COORD_BOOK_F + " = (SELECT DISTINCT " + BOOK_ID_F + " FROM " + BOOKS
								+ " WHERE " + BOOK_NAME_F + " = ?) AND "
								+ COORD_VERSE_F + " = ? ))");
	    st.setString(1, verse.getText());
	    st.setString(2, verse.getBibleVersion().getAbbr());
	    st.setInt(3, verse.getPosition().getChapterNum());
	    st.setString(4, verse.getPosition().getBook().getName());
	    st.setInt(5, verse.getPosition().getVerseNum());
	    commitUpdate(st);
	} catch (SQLException e) {
	    logger.error("Exception caught when inserting the verse {}:", verse, e);
	    throw new BibleStorageException("Verse could not be inserted", e);
	}

    }

    @Override
    public void insertBibleBook(BibleBook book) {
	try {
	    PreparedStatement st = dbConnection.prepareStatement("MERGE INTO " + BOOKS
		    + "(" + BOOK_NAME + ", " + BOOK_DEUT + ") KEY (" + BOOK_NAME + ") VALUES ( ?, ?)");
	    st.setString(1, book.getName());
	    st.setBoolean(2, book.isDeutero());
	    commitUpdate(st);
	} catch (SQLException e) {
	    logger.error("Exception caught when inserting this Bible book: {}",book, e);
	    throw new BibleStorageException("Bible book could not be inserted", e);
	}
    }

    @Override
    public void insertPosition(Position position) {
	try {
	    PreparedStatement st = dbConnection
		    .prepareStatement("MERGE INTO " + COORDS + "(" + COORD_BOOK + ", " + COORD_CHAPT + ", " + COORD_VERSE + ")"
		                      + " KEY ( " + COORD_BOOK + ", " + COORD_CHAPT + ", " + COORD_VERSE + ")"
		                      + " VALUES ((SELECT DISTINCT " + BOOK_ID_F + " FROM " + BOOKS + " WHERE " + BOOK_NAME_F
		                      + " = ?), ?, ?)");
	    st.setString(1, position.getBook().getName());
	    st.setInt(2, position.getChapterNum());
	    st.setInt(3, position.getVerseNum());
	    commitUpdate(st);
	} catch (SQLException e) {
	    logger.error("Exception caught when inserting this Bible position: {}", position, e);
	    throw new BibleStorageException("Position could not be inserted", e);
	}
    }

    @Override
    public void insertBibleVersion(BibleVersion version) {
	try {
	    PreparedStatement st = dbConnection.prepareStatement("MERGE INTO " + VERSIONS + " ("
		    + VERSION_ABBR + ", " + VERSION_LANG + ", " + VERSION_NAME + ") KEY ( " + VERSION_ABBR + " ) VALUES ( ?, ?, ?)");
	    st.setString(1, version.getAbbr());
	    st.setString(2, version.getLanguage());
	    st.setString(3, version.getName());
	    commitUpdate(st);
	} catch (SQLException e) {
	    logger.error("Exception caught when inserting this Bible version: {}", version, e);
	    throw new BibleStorageException("Bible version could not be inserted", e);
	}
    }
    
    @Override
    public BibleVersion getBibleVersion(String abbr) {
	ResultSet rs = null;
	PreparedStatement st = null;
	BibleVersion version = null;

	try {
	    st = dbConnection
		    .prepareStatement("SELECT " + VERSION_NAME_F + ", " + VERSION_ABBR_F + ", " + VERSION_LANG_F
				      + "FROM " + VERSIONS
				      + "WHERE " + VERSION_ABBR_F + " = ? LIMIT 1");
	    st.setString(1, abbr.toLowerCase(new Locale("en")));
	    rs = commitQuery(st);
	    while (rs.next())
		version = new BibleVersion(rs.getString(1), rs.getString(2), rs.getString(3));

	} catch (SQLException e) {
	    logger.error("Exception caught when retrieving the Bible version for this abbreviation: {}", abbr, e);
	    throw new BibleStorageException("Version could not be retrieved", e);
	} finally {
	    try {
		if (rs != null)
		    rs.close();
		if (st != null)
		    st.close();
	    } catch (SQLException e) {
		logger.debug("Exception caught when closing", e);
	    }
	}
	return version;
    }
    
    @Override
    public List<BibleVersion> getAllBibleVersions() {
	ResultSet rs = null;
	PreparedStatement st = null;
	List<BibleVersion> versionList = new ArrayList<BibleVersion>();

	try {
	    st = dbConnection
	    .prepareStatement("SELECT " + VERSION_NAME_F + ", " + VERSION_ABBR_F + ", " + VERSION_LANG_F
			      + "FROM " + VERSIONS
			      + "ORDER BY " + VERSION_ABBR_F);
		
		rs = commitQuery(st);

		while (rs.next())
		    versionList.add(new BibleVersion(rs.getString(1), rs.getString(2), rs.getString(3)));

	} catch (SQLException e) {
	    logger.error("Exception caught when retrieving all the Bible versions", e);
	    throw new BibleStorageException("Bible versions could not be retrieved", e);
	} finally {
	    try {
		if (rs != null)
		    rs.close();
		if (st != null)
		    st.close();
	    } catch (SQLException e) {
		logger.debug("Exception caught when closing", e);
	    }
	}

	return versionList;
    }


    @Override
    public Verse getVerse(Position position, BibleVersion version) {
	ResultSet rs = null;
	PreparedStatement st = null;
	Verse verse = null;

	try {
	    st = dbConnection
		    .prepareStatement("SELECT " + VERSE_TEXT_F + ", " + BOOK_NAME_F + ", " + COORD_CHAPT_F + ", " + COORD_VERSE_F + ", "
		                      + VERSION_NAME_F + ", " + VERSION_ABBR_F + ", " + VERSION_LANG_F
				      + " FROM " + VERSIONS
				      + " INNER JOIN " + VERSES + " ON " + VERSE_VERSION_F + " = " + VERSION_ID_F + " "
				      + " INNER JOIN " + COORDS + " ON " + VERSE_COORD_F + " = " + COORD_ID_F + " "
				      + " INNER JOIN " + BOOKS + " ON " + COORD_BOOK_F + " = " + BOOK_ID_F + " "
				      + " WHERE " + COORD_CHAPT_F + " = ? AND " + BOOK_NAME_F + " = ? AND "
			    + VERSION_ABBR_F + " = ? AND " + COORD_VERSE_F + " = ? LIMIT 1");
	    st.setInt(1, position.getChapterNum());
	    st.setString(2, position.getBook().getName());
	    st.setString(3, version.getAbbr());
	    st.setInt(4, position.getVerseNum());
	    rs = commitQuery(st);
	    while (rs.next())
		verse = new Verse(rs.getString(1), new Position(BibleBook.getBibleBookByName(rs
			.getString(2)), rs.getInt(3), rs.getInt(4)), new BibleVersion(rs.getString(5), rs.getString(6), rs.getString(7)));

	} catch (SQLException e) {
		logger.error("Exception caught when retrieving the verse for the position: {} and Bible version: {}", new Object[] {position, version, e});    
	    throw new BibleStorageException("Verse could not be retrieved", e);
	} finally {
	    try {
		if (rs != null)
		    rs.close();
		if (st != null)
		    st.close();
	    } catch (SQLException e) {
		logger.debug("Exception caught when closing", e);
	    }
	}
	return verse;
    }

    @Override
    public List<Verse> getVerses(List<Position> positions, BibleVersion version) {
	ResultSet rs = null;
	PreparedStatement st = null;
	List<Verse> verseList = new ArrayList<Verse>();

	for (Position position : positions) {

	    try {
		st = dbConnection
		.prepareStatement("SELECT " + VERSE_TEXT_F + ", " + BOOK_NAME_F + ", " + COORD_CHAPT_F + ", " + COORD_VERSE_F + ", "
		                      + VERSION_NAME_F + ", " + VERSION_ABBR_F + ", " + VERSION_LANG_F
				      + " FROM " + VERSIONS
				      + " INNER JOIN " + VERSES + " ON " + VERSE_VERSION_F + " = " + VERSION_ID_F + " "
				      + " INNER JOIN " + COORDS + " ON " + VERSE_COORD_F + " = " + COORD_ID_F + " "
				      + " INNER JOIN " + BOOKS + " ON " + COORD_BOOK_F + " = " + BOOK_ID_F + " "
				      + " WHERE " + COORD_CHAPT_F + " = ? AND " + BOOK_NAME_F + " = ? AND "
			    + VERSION_ABBR_F + " = ? AND " + COORD_VERSE_F + " = ? LIMIT 1");
		st.setInt(1, position.getChapterNum());
		st.setString(2, position.getBook().getName());
		st.setString(3, version.getAbbr());
		st.setInt(4, position.getVerseNum());
		rs = commitQuery(st);
		while (rs.next())
		    verseList.add(new Verse(rs.getString(1),
		                            new Position(BibleBook.getBibleBookByName(rs.getString(2)), rs.getInt(3), rs.getInt(4)),
		                            new BibleVersion(rs.getString(5), rs.getString(6), rs.getString(7))));

	    } catch (SQLException e) {
		logger.error("Exception caught when retrieving the verse for the position: {} and Bible version: {}", new Object[] {position, version, e});    
		throw new BibleStorageException("Verses could not be retrieved", e);
	    } finally {
		try {
		    if (rs != null)
			rs.close();
		    if (st != null)
			st.close();
		} catch (SQLException e) {
		    logger.debug("Exception caught when closing", e);
		}
	    }
	}

	return verseList;
    }
    
    //chapter is represented by Position object with ignored verse number information
    @Override
    public List<Verse> getChapter(Position chapter, BibleVersion version) {
	ResultSet rs = null;
	PreparedStatement st = null;
	List<Verse> verseList = new ArrayList<Verse>();

	    try {
		st = dbConnection
			.prepareStatement("SELECT " + VERSE_TEXT_F + ", " + BOOK_NAME_F + ", " + COORD_CHAPT_F + ", " + COORD_VERSE_F + ", "
			                      + VERSION_NAME_F + ", " + VERSION_ABBR_F + ", " + VERSION_LANG_F
					      + " FROM " + VERSIONS
					      + " INNER JOIN " + VERSES + " ON " + VERSE_VERSION_F + " = " + VERSION_ID_F + " "
					      + " INNER JOIN " + COORDS + " ON " + VERSE_COORD_F + " = " + COORD_ID_F + " "
					      + " INNER JOIN " + BOOKS + " ON " + COORD_BOOK_F + " = " + BOOK_ID_F + " "
					      + "WHERE " + COORD_CHAPT_F + " = ? AND " + BOOK_NAME_F + " = ? AND " + VERSION_ABBR_F + " = ?");
		st.setInt(1, chapter.getChapterNum());
		st.setString(2, chapter.getBook().getName());
		st.setString(3, version.getAbbr());
		rs = commitQuery(st);
		while (rs.next())
		    verseList.add(new Verse(rs.getString(1),
		                            new Position(BibleBook.getBibleBookByName(rs.getString(2)), rs.getInt(3), rs.getInt(4)),
		                            new BibleVersion(rs.getString(5), rs.getString(6), rs.getString(7))));

	    } catch (SQLException e) {
		logger.error("Exception caught when retrieving the verses for the chapter: {} and Bible version: {}", new Object[] {chapter, version, e});
		throw new BibleStorageException("Verses could not be retrieved", e);
	    } finally {
		try {
		    if (rs != null)
		    rs.close();
		    if (st != null)
			st.close();
		} catch (SQLException e) {
		    logger.debug("Exception caught when closing", e);
		}
	    }

	return verseList;
    }

    @Override
    public List<Position> getChapterList(BibleVersion version) {
	PreparedStatement st = null;
	ResultSet rs = null;
	List<Position> chapterList = new ArrayList<Position>();

	try {
	    st = dbConnection
		    .prepareStatement("SELECT DISTINCT " + BOOK_NAME_F + ", " + COORD_CHAPT_F
				    + "FROM " + VERSIONS
				    + "INNER JOIN " + VERSES + " ON " + VERSE_VERSION_F + " = " + VERSION_ID_F + " "
				    + "INNER JOIN " + COORDS + " ON " + VERSE_COORD_F + " = " + COORD_ID_F + " "
				    + "INNER JOIN " + BOOKS + " ON " + COORD_BOOK_F + " = " + BOOK_ID_F + " "
				    + "WHERE " + VERSION_ABBR_F + " = ?");
	    	st.setString(1, version.getAbbr());
		
		rs = commitQuery(st);

		while (rs.next())
		    chapterList.add(new Position(BibleBook.getBibleBookByName(rs.getString(1)),
		                                 rs.getInt(2), 0));

	} catch (SQLException e) {
	    logger.error("Exception caught when retrieving the chapters for Bible version: {}", version, e);
	    throw new BibleStorageException("Chapters could not be retrieved", e);
	} finally {
	    try {
		if (rs != null)
		    rs.close();
		if (st != null)
		    st.close();
	    } catch (SQLException e) {
		logger.debug("Exception caught when closing", e);
	    }
	}
	
	Collections.sort(chapterList);

	return chapterList;
    }


    @Override
    public List<Verse> compareVerses(Position position, List<BibleVersion> versions) {
	PreparedStatement st = null;
	ResultSet rs = null;
	List<Verse> verseList = new ArrayList<Verse>();

	try {
	    st = dbConnection
		    .prepareStatement("SELECT " + VERSE_TEXT_F + ", " + BOOK_NAME_F + ", " + COORD_CHAPT_F + ", " + COORD_VERSE_F + ", "
		                      + VERSION_NAME_F + ", " + VERSION_ABBR_F + ", " + VERSION_LANG_F
				      + " FROM " + VERSIONS
				      + " INNER JOIN " + VERSES + " ON " + VERSE_VERSION_F + " = " + VERSION_ID_F + " "
				      + " INNER JOIN " + COORDS + " ON " + VERSE_COORD_F + " = " + COORD_ID_F + " "
				      + " INNER JOIN " + BOOKS + " ON " + COORD_BOOK_F + " = " + BOOK_ID_F + " "
				      + " WHERE " + COORD_CHAPT_F + " = ? AND " + BOOK_NAME_F + " = ? AND "
				    + VERSION_ABBR_F + " = ? AND " + COORD_VERSE_F + " = ? LIMIT 1");
	    for (BibleVersion version : versions) {

		st.setInt(1, position.getChapterNum());
		st.setString(2, position.getBook().getName());
		st.setString(3, version.getAbbr());
		st.setInt(4, position.getVerseNum());

		rs = commitQuery(st);

		while (rs.next())
		    verseList.add(new Verse(rs.getString(1),
		                            new Position(BibleBook.getBibleBookByName(rs.getString(2)), rs.getInt(3), rs.getInt(4)),
		                            new BibleVersion(rs.getString(5), rs.getString(6), rs.getString(7))));

	    }

	} catch (SQLException e) {
		logger.error("Exception caught when retrieving the verses for the position: {} and Bible versions: {}", new Object[] {position, versions, e});    
	    throw new BibleStorageException("Verses could not be retrieved", e);
	} finally {
	    try {
		if (rs != null)
		    rs.close();
		if (st != null)
		    st.close();
	    } catch (SQLException e) {
		logger.debug("Exception caught when closing", e);
	    }
	}

	return verseList;
    }

    @Override
    public List<Verse> compareVerses(List<Position> positions, List<BibleVersion> versions) {
	ResultSet rs = null;
	PreparedStatement st = null;
	List<Verse> verseList = new ArrayList<Verse>();

	for (BibleVersion version : versions) {
	    for (Position position : positions) {
		try {
		    st = dbConnection
			    .prepareStatement("SELECT " + VERSE_TEXT_F + ", " + BOOK_NAME_F + ", " + COORD_CHAPT_F + ", " + COORD_VERSE_F + ", "
			                      + VERSION_NAME_F + ", " + VERSION_ABBR_F + ", " + VERSION_LANG_F
					      + " FROM " + VERSIONS
					      + " INNER JOIN " + VERSES + " ON " + VERSE_VERSION_F + " = " + VERSION_ID_F + " "
					      + " INNER JOIN " + COORDS + " ON " + VERSE_COORD_F + " = " + COORD_ID_F + " "
					      + " INNER JOIN " + BOOKS + " ON " + COORD_BOOK_F + " = " + BOOK_ID_F + " "
					      + " WHERE " + COORD_CHAPT_F + " = ? AND " + BOOK_NAME_F + " = ? "
					      + " AND " + VERSION_ABBR_F + " = ? AND " + COORD_VERSE_F + " = ? LIMIT 1");
		    st.setInt(1, position.getChapterNum());
		    st.setString(2, position.getBook().getName());
		    st.setString(3, version.getAbbr());
		    st.setInt(4, position.getVerseNum());
		    rs = commitQuery(st);
		    while (rs.next())
			    verseList.add(new Verse(rs.getString(1),
			                            new Position(BibleBook.getBibleBookByName(rs.getString(2)), rs.getInt(3), rs.getInt(4)),
			                            new BibleVersion(rs.getString(5), rs.getString(6), rs.getString(7))));

		} catch (SQLException e) {
			logger.error("Exception caught when retrieving the verse for the position: {} and Bible version: {}", new Object[] {position, version, e});    
		    throw new BibleStorageException("Verses could not be retrieved", e);
		} finally {
		    try {
			if (rs != null)
			    rs.close();
			if (st != null)
			    st.close();
		    } catch (SQLException e) {
			logger.debug("Exception caught when closing", e);
		    }
		}
	    }
	}

	return verseList;
    }

    @Override
    public void insertBookmark(Bookmark bookmark) {
	try {
	    PreparedStatement st = dbConnection.prepareStatement(
		    "INSERT INTO " + BKMARKS
			    + "(" + BKMARK_VERSE + ", " + BKMARK_NAME + ") VALUES"
			    + "((SELECT DISTINCT " + VERSE_ID_F + " FROM " + VERSES + " WHERE "
					+ VERSE_VERSION_F + " = (SELECT DISTINCT " + VERSION_ID_F + " FROM " + VERSIONS + " WHERE "
									+ VERSION_ABBR_F + " = ?) AND "
					+ VERSE_COORD_F + " = (SELECT DISTINCT " + COORD_ID_F + " FROM " + COORDS + " WHERE"
									+ COORD_BOOK_F + " = (SELECT DISTINCT "
											+ BOOK_ID_F + " FROM " + BOOKS + " WHERE "
											+ BOOK_NAME_F + " = ?) AND "
									+ COORD_CHAPT_F + " = ? AND "
									+ COORD_VERSE_F + " = ?))" + ", ?)");

	    st.setString(1, bookmark.getVerse().getBibleVersion().getAbbr());
	    st.setString(2, bookmark.getVerse().getPosition().getBook().getName());
	    st.setInt(3, bookmark.getVerse().getPosition().getChapterNum());
	    st.setInt(4, bookmark.getVerse().getPosition().getVerseNum());
	    st.setString(5, bookmark.getName());
	    commitUpdate(st);
	} catch (SQLException e) {
	    logger.error("Exception caught when inserting the bookmark: {}", bookmark, e);
	    throw new BibleStorageException("Bookmark could not be inserted", e);
	}

    }

    //TODO add unit test
    @Override
    public int deleteBookmark(String bookmarkName) {
	int bookmarksDeleted = 0;
	try {
	    PreparedStatement st = dbConnection
			    .prepareStatement("DELETE FROM " + BKMARKS + " WHERE " + BKMARK_NAME_F + " = ?");
	    st.setString(1, bookmarkName);
	    bookmarksDeleted = commitUpdate(st);
	} catch (SQLException e) {
	    logger.error("Exception caught when deleting the bookmark with name: {}",bookmarkName, e);
	    throw new BibleStorageException("Bookmark could not be deleted", e);
	}
	return bookmarksDeleted;
    }

    @Override
    public List<Bookmark> getBookmarks() {
	ResultSet rs = null;
	PreparedStatement st = null;
	List<Bookmark> bookmarkList = new ArrayList<Bookmark>();

	try {
	    st = dbConnection
			.prepareStatement("SELECT " + BKMARK_NAME_F + ", " + VERSE_TEXT_F + ", " + VERSION_NAME_F + ", " + VERSION_ABBR_F + ", "
				+ VERSION_LANG_F + ", " + COORD_VERSE_F + ", " + COORD_CHAPT_F + ", " + BOOK_NAME_F
				+ "FROM " + BKMARKS
					    + "INNER JOIN " + VERSES + " ON " + BKMARK_VERSE_F + " = " + VERSE_ID_F + " "
					    + "INNER JOIN " + VERSIONS + " ON " + VERSE_VERSION_F + " = " + VERSION_ID_F + " "
					    + "INNER JOIN " + COORDS + " ON " + VERSE_COORD_F + " = " + COORD_ID_F + " "
					    + "INNER JOIN " + BOOKS + " ON " + COORD_BOOK_F + " = " + BOOK_ID_F);
	    rs = commitQuery(st);
	    while (rs.next())
		bookmarkList.add(new Bookmark(rs.getString(1),
		                              new Verse(rs.getString(2),
		                                        new Position(BibleBook.getBibleBookByName(rs.getString(8)), rs.getInt(7), rs.getInt(6)),
		                                        new BibleVersion(rs.getString(3), rs.getString(4), rs.getString(5)))));

	} catch (SQLException e) {
	    logger.error("Exception caught when retrieving all the bookmarks", e);
	    throw new BibleStorageException("Bookmarks could not be retrieved", e);
	} finally {
	    try {
		if (rs != null)
		    rs.close();
		if (st != null)
		    st.close();
	    } catch (SQLException e) {
		logger.debug("Exception caught when closing", e);
	    }
	}

	return bookmarkList;
    }

    @Override
    public List<Bookmark> getBookmarks(BibleVersion version) {
	ResultSet rs = null;
	PreparedStatement st = null;
	List<Bookmark> bookmarkList = new ArrayList<Bookmark>();

	try {
	    st = dbConnection
			.prepareStatement("SELECT " + BKMARK_NAME_F + ", " + VERSE_TEXT_F + ", " + VERSION_NAME_F + ", " + VERSION_ABBR_F + ", "
						+ VERSION_LANG_F + ", " + COORD_VERSE_F + ", " + COORD_CHAPT_F + ", " + BOOK_NAME_F
						+ "FROM " + BKMARKS
						+ "INNER JOIN " + VERSES + " ON " + BKMARK_VERSE_F + " = " + VERSE_ID_F + " "
						+ "INNER JOIN " + VERSIONS + " ON " + VERSE_VERSION_F + " = " + VERSION_ID_F + " "
						+ "INNER JOIN " + COORDS + " ON " + VERSE_COORD_F + " = " + COORD_ID_F + " "
						+ "INNER JOIN " + BOOKS + " ON " + COORD_BOOK_F + " = " + BOOK_ID_F
						+ "WHERE " + VERSION_ABBR_F + " = ?");
	    st.setString(1, version.getAbbr());
	    rs = commitQuery(st);
	    while (rs.next())
		bookmarkList.add(new Bookmark(rs.getString(1),
		                              new Verse(rs.getString(2),
		                                        new Position(BibleBook.getBibleBookByName(rs.getString(8)), rs.getInt(7), rs.getInt(6)),
		                                        new BibleVersion(rs.getString(3), rs.getString(4), rs.getString(5)))));

	} catch (SQLException e) {
	    logger.error("Exception caught when retrieving the bookmarks for the Bible version: {}", version, e);
	    throw new BibleStorageException("Bookmarks could not be retrieved", e);
	} finally {
	    try {
		if (rs != null)
		    rs.close();
		if (st != null)
		    st.close();
	    } catch (SQLException e) {
		logger.debug("Exception caught when closing", e);
	    }
	}

	return bookmarkList;
    }

    @Override
    public void insertNote(Note note) {
	try {
	    PreparedStatement st = dbConnection.prepareStatement(
		    "INSERT INTO " + NOTES
			    + "(" + NOTE_TYPE + ", " + NOTE_TEXT + ", " + NOTE_COORD + ") VALUES"
			    + "(?, ?, (SELECT DISTINCT " + COORD_ID_F + " FROM " + COORDS + " WHERE"
							+ COORD_BOOK_F + " = (SELECT DISTINCT "
											+ BOOK_ID_F + " FROM " + BOOKS + " WHERE "
											+ BOOK_NAME_F + " = ?) AND "
							+ COORD_CHAPT_F + " = ? AND "
							+ COORD_VERSE_F + " = ?))");

	    st.setString(1, Character.toString(note.getType().getSpecifyingChar()));
	    st.setString(2, note.getText());
	    st.setString(3, note.getPosition().getBook().getName());
	    st.setInt(4, note.getPosition().getChapterNum());
	    st.setInt(5, note.getPosition().getVerseNum());
	    commitUpdate(st);
	} catch (SQLException e) {
	    logger.error("Exception caught when inserting the note: {}", note, e);
	    throw new BibleStorageException("Note could not be inserted", e);
	}

    }

    //TODO add unit test
    @Override
    public int deleteNote(Position position) {
	int notesDeleted = 0;
	try {
	    PreparedStatement st = dbConnection
		.prepareStatement("DELETE FROM " + NOTES
		                  + " WHERE " + NOTE_COORD_F + " = (SELECT DISTINCT " + COORD_ID_F + " FROM " + COORDS
			+ " INNER JOIN " + BOOKS + " ON " + COORD_BOOK_F + " = " + BOOK_ID_F
			+ " WHERE " + BOOK_NAME_F + " = ? AND " + COORD_CHAPT_F + " = ? AND " + COORD_VERSE_F + "= ?) LIMIT 1");
    st.setString(1, position.getBook().getName());
    st.setInt(2, position.getChapterNum());
    st.setInt(3, position.getVerseNum());
	    notesDeleted = commitUpdate(st);
	} catch (SQLException e) {
	    logger.error("Exception caught when deleting note for position: {}", position, e);
	    throw new BibleStorageException("Note could not be deleted", e);
	}
	return notesDeleted;
    }

    @Override
    public List<Note> getNotes(Position position) {
	ResultSet rs = null;
	PreparedStatement st = null;
	List<Note> noteList = new ArrayList<Note>();

	try {
	    st = dbConnection
			.prepareStatement("SELECT " + NOTE_TEXT_F + ", " + BOOK_NAME_F + ", " + COORD_CHAPT_F + ", "
					  + COORD_VERSE_F + ", " + NOTE_TYPE_F + "FROM " + NOTES
				+ " INNER JOIN " + COORDS + " ON " + NOTE_COORD_F + " = " + COORD_ID_F + " "
				+ " INNER JOIN " + BOOKS + " ON " + COORD_BOOK_F + " = " + BOOK_ID_F
				+ " WHERE " + BOOK_NAME_F + " = ? AND " + COORD_CHAPT_F + " = ? AND " + COORD_VERSE_F + "= ?");
	    st.setString(1, position.getBook().getName());
	    st.setInt(2, position.getChapterNum());
	    st.setInt(3, position.getVerseNum());
	    rs = commitQuery(st);
	    while (rs.next())
		noteList.add(new Note(rs.getString(1),
					  new Position(BibleBook.getBibleBookByName(rs.getString(2)), rs
						  .getInt(3), rs.getInt(4)), Note.getNoteTypeByChar(rs
						  .getString(5).charAt(0))));

	} catch (SQLException e) {
	    logger.error("Exception caught when retrieving the notes for position: {}", position, e);
	    throw new BibleStorageException("Notes could not be retrieved", e);
	} finally {
	    try {
		if (rs != null)
		    rs.close();
		if (st != null)
		    st.close();
	    } catch (SQLException e) {
		logger.debug("Exception caught when closing", e);
	    }
	}

	return noteList;
    }
    
    //TODO add unit test
    @Override
    public List<Note> getNotesForChapter(Position chapter) {
	ResultSet rs = null;
	PreparedStatement st = null;
	List<Note> noteList = new ArrayList<Note>();

	try {
	    st = dbConnection
			.prepareStatement("SELECT " + NOTE_TEXT_F + ", " + BOOK_NAME_F + ", " + COORD_CHAPT_F + ", "
					  + COORD_VERSE_F + ", " + NOTE_TYPE_F + "FROM " + NOTES
				+ " INNER JOIN " + COORDS + " ON " + NOTE_COORD_F + " = " + COORD_ID_F + " "
				+ " INNER JOIN " + BOOKS + " ON " + COORD_BOOK_F + " = " + BOOK_ID_F
				+ " WHERE " + BOOK_NAME_F + " = ? AND " + COORD_CHAPT_F + " = ?");
	    st.setString(1, chapter.getBook().getName());
	    st.setInt(2, chapter.getChapterNum());
	    rs = commitQuery(st);
	    while (rs.next())
		noteList.add(new Note(rs.getString(1),
					  new Position(BibleBook.getBibleBookByName(rs.getString(2)), rs
						  .getInt(3), rs.getInt(4)), Note.getNoteTypeByChar(rs
						  .getString(5).charAt(0))));

	} catch (SQLException e) {
	    logger.error("Exception caught when retrieving the notes for chapter: {}", chapter, e);
	    throw new BibleStorageException("Notes could not be retrieved", e);
	} finally {
	    try {
		if (rs != null)
		    rs.close();
		if (st != null)
		    st.close();
	    } catch (SQLException e) {
		logger.debug("Exception caught when closing", e);
	    }
	}

	return noteList;
    }

    @Override
    public List<Verse> searchVersesForText(String text) {
	ResultSet rs = null;
	PreparedStatement st = null;
	List<Verse> verseList = new ArrayList<Verse>();

	try {
	    st = dbConnection
			.prepareStatement("SELECT " + VERSE_TEXT_F + ", " + BOOK_NAME_F + ", " + COORD_CHAPT_F + ", " + COORD_VERSE_F + ", "
			                    + VERSION_NAME_F + ", " + VERSION_ABBR_F + ", " + VERSION_LANG_F
					    + " FROM FT_SEARCH_DATA(?, 0, 0) FT"
					    + " INNER JOIN " + VERSES + " ON FT.TABLE = 'VERSES' AND " + VERSE_ID_F + " = FT.KEYS[0]"
					    + " INNER JOIN " + VERSIONS + " ON " + VERSE_VERSION_F + " = " + VERSION_ID_F + " "
					    + " INNER JOIN " + COORDS + " ON " + VERSE_COORD_F + " = " + COORD_ID_F + " "
					    + " INNER JOIN " + BOOKS + " ON " + COORD_BOOK_F + " = " + BOOK_ID_F
					    + " ORDER BY" + VERSION_ABBR_F);
	    st.setString(1, text);
	    rs = commitQuery(st);
	    while (rs.next())
		    verseList.add(new Verse(rs.getString(1),
		                            new Position(BibleBook.getBibleBookByName(rs.getString(2)), rs.getInt(3), rs.getInt(4)),
		                            new BibleVersion(rs.getString(5), rs.getString(6), rs.getString(7))));

	} catch (SQLException e) {
	    logger.error("Exception caught when searching the verses for the text: {}", text, e);
	    throw new BibleStorageException("Verses could not be searched", e);
	} finally {
	    try {
		if (rs != null)
		    rs.close();
		if (st != null)
		    st.close();
	    } catch (SQLException e) {
		logger.debug("Exception caught when closing", e);
	    }
	}

	return verseList;
    }

    //TODO add unit test
    @Override
    public List<Verse> searchVersesForText(String text, BibleVersion version) {
	ResultSet rs = null;
	PreparedStatement st = null;
	List<Verse> verseList = new ArrayList<Verse>();

	try {
	    st = dbConnection
			.prepareStatement("SELECT " + VERSE_TEXT_F + ", " + BOOK_NAME_F + ", " + COORD_CHAPT_F + ", " + COORD_VERSE_F + ", "
			                    + VERSION_NAME_F + ", " + VERSION_ABBR_F + ", " + VERSION_LANG_F
					    + " FROM FT_SEARCH_DATA(?, 0, 0) FT"
					    + " INNER JOIN " + VERSES + " ON FT.TABLE = 'VERSES' AND " + VERSE_ID_F + " = FT.KEYS[0]"
					    + " INNER JOIN " + VERSIONS + " ON " + VERSE_VERSION_F + " = " + VERSION_ID_F + " AND" + VERSION_ABBR_F + " = ?"
					    + " INNER JOIN " + COORDS + " ON " + VERSE_COORD_F + " = " + COORD_ID_F
					    + " INNER JOIN " + BOOKS + " ON " + COORD_BOOK_F + " = " + BOOK_ID_F
					    + " ORDER BY" + VERSION_ABBR_F);
	    st.setString(1, text);
	    st.setString(2, version.getAbbr());
	    rs = commitQuery(st);
	    while (rs.next())
		    verseList.add(new Verse(rs.getString(1),
		                            new Position(BibleBook.getBibleBookByName(rs.getString(2)), rs.getInt(3), rs.getInt(4)),
		                            new BibleVersion(rs.getString(5), rs.getString(6), rs.getString(7))));

	} catch (SQLException e) {
	    logger.error("Exception caught when searching the verses for the text: {} in Bible version: {}", new Object[] {text, version, e});
	    throw new BibleStorageException("Verses could not be searched", e);
	} finally {
	    try {
		if (rs != null)
		    rs.close();
		if (st != null)
		    st.close();
	    } catch (SQLException e) {
		logger.debug("Exception caught when closing", e);
	    }
	}
	
	return verseList;
    }

    //TODO add unit test
    @Override
    public List<Verse> searchVersesForText(String text, BibleBook book) {
	ResultSet rs = null;
	PreparedStatement st = null;
	List<Verse> verseList = new ArrayList<Verse>();

	try {
	    st = dbConnection
			.prepareStatement("SELECT " + VERSE_TEXT_F + ", " + BOOK_NAME_F + ", " + COORD_CHAPT_F + ", " + COORD_VERSE_F + ", "
			                    + VERSION_NAME_F + ", " + VERSION_ABBR_F + ", " + VERSION_LANG_F
					    + " FROM FT_SEARCH_DATA(?, 0, 0) FT"
					    + " INNER JOIN " + VERSES + " ON FT.TABLE = 'VERSES' AND " + VERSE_ID_F + " = FT.KEYS[0]"
					    + " INNER JOIN " + COORDS + " ON " + VERSE_COORD_F + " = " + COORD_ID_F
					    + " INNER JOIN " + BOOKS + " ON " + COORD_BOOK_F + " = " + BOOK_ID_F + " AND" + BOOK_NAME_F + " = ?"
					    + " INNER JOIN " + VERSIONS + " ON " + VERSE_VERSION_F + " = " + VERSION_ID_F
					    + " ORDER BY" + VERSION_ABBR_F);
	    st.setString(1, text);
	    st.setString(2, book.getName());
	    rs = commitQuery(st);
	    while (rs.next())
		    verseList.add(new Verse(rs.getString(1),
		                            new Position(BibleBook.getBibleBookByName(rs.getString(2)), rs.getInt(3), rs.getInt(4)),
		                            new BibleVersion(rs.getString(5), rs.getString(6), rs.getString(7))));

	} catch (SQLException e) {
	    logger.error("Exception caught when searching the verses for the text: {} in Bible book: {}", new Object[] {text, book, e});
	    throw new BibleStorageException("Verses could not be searched", e);
	} finally {
	    try {
		if (rs != null)
		    rs.close();
		if (st != null)
		    st.close();
	    } catch (SQLException e) {
		logger.debug("Exception caught when closing", e);
	    }
	}

	return verseList;
    }

    //TODO add unit test
    @Override
    public List<Verse> searchVersesForText(String text, BibleBook book, BibleVersion version) {
	ResultSet rs = null;
	PreparedStatement st = null;
	List<Verse> verseList = new ArrayList<Verse>();

	try {
	    st = dbConnection
			.prepareStatement("SELECT " + VERSE_TEXT_F + ", " + BOOK_NAME_F + ", " + COORD_CHAPT_F + ", " + COORD_VERSE_F + ", "
			                    + VERSION_NAME_F + ", " + VERSION_ABBR_F + ", " + VERSION_LANG_F
					    + " FROM FT_SEARCH_DATA(?, 0, 0) FT"
					    + " INNER JOIN " + VERSES + " ON FT.TABLE = 'VERSES' AND " + VERSE_ID_F + " = FT.KEYS[0]"
					    + " INNER JOIN " + COORDS + " ON " + VERSE_COORD_F + " = " + COORD_ID_F
					    + " INNER JOIN " + BOOKS + " ON " + COORD_BOOK_F + " = " + BOOK_ID_F + " AND" + BOOK_NAME_F + " = ?"
					    + " INNER JOIN " + VERSIONS + " ON " + VERSE_VERSION_F + " = " + VERSION_ID_F + " AND" + VERSION_ABBR_F + " = ?"
					    + " ORDER BY" + VERSION_ABBR_F);
	    st.setString(1, text);
	    st.setString(2, book.getName());
	    st.setString(3, version.getAbbr());
	    rs = commitQuery(st);
	    while (rs.next())
		    verseList.add(new Verse(rs.getString(1),
		                            new Position(BibleBook.getBibleBookByName(rs.getString(2)), rs.getInt(3), rs.getInt(4)),
		                            new BibleVersion(rs.getString(5), rs.getString(6), rs.getString(7))));

	} catch (SQLException e) {
	    logger.error("Exception caught when searching the verses for the text: {} in Bible book: {} and Bible version: {}", new Object[] {text, book, version, e});
	    throw new BibleStorageException("Verses could not be searched", e);
	} finally {
	    try {
		if (rs != null)
		    rs.close();
		if (st != null)
		    st.close();
	    } catch (SQLException e) {
		logger.debug("Exception caught when closing", e);
	    }
	}

	return verseList;
    }

}

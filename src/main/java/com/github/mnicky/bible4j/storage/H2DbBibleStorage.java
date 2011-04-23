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
import static com.github.mnicky.bible4j.storage.H2DbNaming.*;

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
	    con.setAutoCommit(false);
	    result = st.executeQuery();
	    con.commit();
	} catch (SQLException e) {
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

	    // FIXME add CASCADE or RESTRICTED to foreign keys etc?
	    // TODO convert more VARCHARs to V_IGNORECASE?
	    // TODO add more UNIQUE constraints, CHECK etc... ?

	    st.addBatch("CREATE TABLE IF NOT EXISTS " + VERSIONS + " ("
		    + VERSION_ID + " INT IDENTITY NOT NULL,"
		    + VERSION_NAME + " VARCHAR_IGNORECASE(50) NOT NULL UNIQUE,"
		    + VERSION_LANG + " VARCHAR(50) NOT NULL)");

	    st.addBatch("CREATE TABLE IF NOT EXISTS " + BOOKS + " ("
		    + BOOK_ID + " INT IDENTITY NOT NULL,"
		    + BOOK_NAME + " VARCHAR_IGNORECASE(50) NOT NULL UNIQUE,"
		    + BOOK_DEUT + " BOOLEAN NOT NULL)");

	    st.addBatch("CREATE TABLE IF NOT EXISTS " + COORDS + " ("
		    + COORD_ID + " INT IDENTITY NOT NULL,"
		    + COORD_BOOK + " INT NOT NULL,"
		    + COORD_CHAPT + " INT NOT NULL,"
		    + COORD_VERSE + " INT NOT NULL,"
		    + "FOREIGN KEY (" + COORD_BOOK + ") REFERENCES " + BOOKS + ")");

	    st.addBatch("CREATE TABLE IF NOT EXISTS " + VERSES + " ("
		    + VERSE_ID + " INT IDENTITY NOT NULL,"
		    + VERSE_TEXT + " VARCHAR(500) NOT NULL,"
		    + VERSE_VERSION + " INT NOT NULL,"
		    + VERSE_COORD + " INT NOT NULL,"
		    + "FOREIGN KEY (" + VERSE_VERSION + ") REFERENCES " + VERSIONS + ","
		    + "FOREIGN KEY (" + VERSE_COORD + ") REFERENCES " + COORDS + ")");

	    st.addBatch("CREATE TABLE IF NOT EXISTS " + NOTES + " ("
		    + NOTE_ID + " INT IDENTITY NOT NULL,"
		    + NOTE_TYPE + " VARCHAR(1) NOT NULL,"
		    + NOTE_TEXT + " VARCHAR(500) NOT NULL,"
		    + NOTE_COORD + " INT NOT NULL,"
		    + "FOREIGN KEY (" + NOTE_COORD + ") REFERENCES " + COORDS + ")");

	    st.addBatch("CREATE TABLE IF NOT EXISTS " + BKMARKS + " ("
		    + BKMARK_ID + " INT IDENTITY NOT NULL,"
		    + BKMARK_NAME + " VARCHAR(50) NOT NULL,"
		    + BKMARK_VERSE + " INT NOT NULL,"
		    + "FOREIGN KEY (" + BKMARK_VERSE + ") REFERENCES " + VERSES + ")");

	    st.addBatch("CREATE TABLE IF NOT EXISTS " + RLISTS + " ("
		    + RLIST_ID + " INT IDENTITY NOT NULL,"
		    + RLIST_NAME + " VARCHAR(50) NOT NULL UNIQUE)");

	    st.addBatch("CREATE TABLE IF NOT EXISTS " + READS + " ("
		    + READ_ID + " INT IDENTITY NOT NULL,"
		    + READ_DATE + " DATE NOT NULL,"
		    + READ_LIST + " INT NOT NULL,"
		    + "FOREIGN KEY (" + READ_LIST + ") REFERENCES " + RLISTS + ")");

	    st.addBatch("CREATE TABLE IF NOT EXISTS " + READxCOORDS + " ("
		    + READxCOORD_ID + " INT IDENTITY NOT NULL,"
		    + READxCOORD_COORD + " INT NOT NULL,"
		    + READxCOORD_READ + " INT NOT NULL,"
		    + "FOREIGN KEY (" + READxCOORD_COORD + ") REFERENCES " + COORDS + ","
		    + "FOREIGN KEY (" + READxCOORD_READ + ") REFERENCES " + READS + ")");

	    st.addBatch("CREATE TABLE IF NOT EXISTS " + TERMS + " ("
		    + TERM_ID + " INT IDENTITY NOT NULL,"
		    + TERM_NAME + " VARCHAR(50) NOT NULL UNIQUE,"
		    + TERM_DEF + " VARCHAR(500) NOT NULL)");

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
		    .prepareStatement("INSERT INTO " + VERSES + " (" + VERSE_TEXT + ", " + VERSE_VERSION
			    + ", " + VERSE_COORD + ") VALUES "
			    + "( ?,"
			    + "(SELECT DISTINCT " + VERSION_ID_F + " FROM " + VERSIONS + " WHERE "
			    + VERSION_NAME_F + " = ?),"
			    + "(SELECT DISTINCT " + COORD_ID_F + " FROM " + COORDS + " WHERE "
			    + COORD_CHAPT_F
			    + " = ? AND " + COORD_BOOK_F + " = (SELECT DISTINCT " + BOOK_ID_F + " FROM "
			    + BOOKS
			    + " WHERE " + BOOK_NAME_F + " = ?) AND " + COORD_VERSE_F + " = ? ))");
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
	    PreparedStatement st = dbConnection.prepareStatement("INSERT INTO " + BOOKS
		    + "(" + BOOK_NAME + ", " + BOOK_DEUT + ") VALUES ( ?, ?)");
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
		    .prepareStatement("INSERT INTO " + COORDS + "("+ COORD_BOOK + ", " + COORD_CHAPT + ", " + COORD_VERSE + ")" +
		    		"VALUES ((SELECT DISTINCT " + BOOK_ID_F + " FROM " + BOOKS + " WHERE " + BOOK_NAME_F + " = ?), ?, ?)");
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
	    PreparedStatement st = dbConnection.prepareStatement("INSERT INTO " + VERSIONS + " (" + VERSION_NAME + ", " + VERSION_LANG + ") VALUES ( ?, ?)");
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
		    .prepareStatement("SELECT "+VERSE_TEXT_F+", "+VERSION_NAME_F+", "+VERSION_LANG_F+", "+COORD_VERSE_F+", "+COORD_CHAPT_F+", "+BOOK_NAME_F
		                      + "FROM " + VERSIONS
		                      + "INNER JOIN "+VERSES+" ON "+VERSE_VERSION_F+" = "+VERSION_ID_F+" "
		                      + "INNER JOIN "+COORDS+" ON "+VERSE_COORD_F+" = "+COORD_ID_F+" "
		                      + "INNER JOIN "+BOOKS+" ON "+COORD_BOOK_F+" = "+BOOK_ID_F+" "
		                      + "WHERE "+COORD_CHAPT_F+" = ? AND "+BOOK_NAME_F+" = ? AND "+VERSION_NAME_F+" = ? AND "+COORD_VERSE_F+" = ? LIMIT 1");
	    st.setInt(1, position.getChapterNum());
	    st.setString(2, position.getBook().getName());
	    st.setString(3, version.getName());
	    st.setInt(4, position.getVerseNum());
	    rs = commitQuery(st);
	    while (rs.next())
		verse = new Verse(rs.getString(1), new Position(BibleBook.getBibleBookByName(rs
			.getString(6)), rs.getInt(5), rs.getInt(4)),
			new BibleVersion(rs.getString(2), rs.getString(3)));

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
		    .prepareStatement("SELECT "+VERSE_TEXT_F+", "+VERSION_NAME_F+", "+VERSION_LANG_F+", "+COORD_VERSE_F+", "+COORD_CHAPT_F+", "+BOOK_NAME_F
					    + "FROM " + VERSIONS
					    + "INNER JOIN "+VERSES+" ON "+VERSE_VERSION_F+" = "+VERSION_ID_F+" "
					    + "INNER JOIN "+COORDS+" ON "+VERSE_COORD_F+" = "+COORD_ID_F+" "
					    + "INNER JOIN "+BOOKS+" ON "+COORD_BOOK_F+" = "+BOOK_ID_F+" "
					    + "WHERE "+COORD_CHAPT_F+" = ? AND "+BOOK_NAME_F+" = ? AND "+VERSION_NAME_F+" = ? AND "+COORD_VERSE_F+" = ? LIMIT 1");
		st.setInt(1, position.getChapterNum());
		st.setString(2, position.getBook().getName());
		st.setString(3, version.getName());
		st.setInt(4, position.getVerseNum());
		rs = commitQuery(st);
		while (rs.next())
		    verseList.add(new Verse(rs.getString(1), new Position(BibleBook
			    .getBibleBookByName(rs
				    .getString(6)), rs.getInt(5), rs
			    .getInt(4)), new BibleVersion(rs.getString(2), rs
			    .getString(3))));

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
	PreparedStatement st = null;
	List<Verse> verseList = new ArrayList<Verse>();

	try {
	    st = dbConnection
	    .prepareStatement("SELECT "+VERSE_TEXT_F+", "+VERSION_NAME_F+", "+VERSION_LANG_F+", "+COORD_VERSE_F+", "+COORD_CHAPT_F+", "+BOOK_NAME_F
				    + "FROM " + VERSIONS
				    + "INNER JOIN "+VERSES+" ON "+VERSE_VERSION_F+" = "+VERSION_ID_F+" "
				    + "INNER JOIN "+COORDS+" ON "+VERSE_COORD_F+" = "+COORD_ID_F+" "
				    + "INNER JOIN "+BOOKS+" ON "+COORD_BOOK_F+" = "+BOOK_ID_F+" "
				    + "WHERE "+COORD_CHAPT_F+" = ? AND "+BOOK_NAME_F+" = ? AND "+VERSION_NAME_F+" = ? AND "+COORD_VERSE_F+" = ? LIMIT 1");
	    for (BibleVersion version : versions) {

		st.setInt(1, position.getChapterNum());
		st.setString(2, position.getBook().getName());
		st.setString(3, version.getName());
		st.setInt(4, position.getVerseNum());

		ResultSet rs = commitQuery(st);

		while (rs.next())
		    verseList.add(new Verse(rs.getString(1), new Position(BibleBook
			    .getBibleBookByName(rs
				    .getString(6)), rs.getInt(5), rs
			    .getInt(4)), new BibleVersion(rs.getString(2), rs
			    .getString(3))));

		rs.close();

	    }

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

	return verseList;
    }

    // seems to inefficient
    // @Override
    // public List<Verse> compareVersesOptimalized(Position position, List<BibleVersion> versions)
    // throws BibleStorageException {
    //
    // List<Verse> verseList = new ArrayList<Verse>();
    //
    // // prepare batch select query part for IN statement
    // final int SINGLE_BATCH = 1;
    // final int SMALL_BATCH = 4;
    // final int MEDIUM_BATCH = 11;
    // final int LARGE_BATCH = 51;
    //
    // int totalNumberOfValuesLeftToBatch = versions.size();
    // int versionNameToInsert = 0;
    //
    // while (totalNumberOfValuesLeftToBatch > 0) {
    //
    // PreparedStatement st = null;
    //
    // int batchSize = SINGLE_BATCH;
    //
    // if (totalNumberOfValuesLeftToBatch >= LARGE_BATCH)
    // batchSize = LARGE_BATCH;
    // else if (totalNumberOfValuesLeftToBatch >= MEDIUM_BATCH)
    // batchSize = MEDIUM_BATCH;
    // else if (totalNumberOfValuesLeftToBatch >= SMALL_BATCH)
    // batchSize = SMALL_BATCH;
    //
    // totalNumberOfValuesLeftToBatch -= batchSize;
    //
    // StringBuilder inClause = new StringBuilder();
    // for (int i = 0; i < batchSize - 1; i++)
    // inClause.append("?, ");
    // inClause.append('?');
    //
    // // run the query
    //
    // try {
    // st = dbConnection
    // .prepareStatement("SELECT `text`, `bible_versions`.`name` AS version, `lang`, `verse_num`, `chapter_num`, `bible_books`.`name` AS `book` "
    // + "FROM bible_versions "
    // + "INNER JOIN `verses` ON `bible_version_id` = `bible_versions`.`id` "
    // + "INNER JOIN `coords` ON `coord_id` = `coords`.`id` "
    // + "INNER JOIN `bible_books` ON `bible_book_id` = `bible_books`.`id` "
    // + "WHERE `chapter_num` = ? AND `bible_books`.`name` = ? AND `bible_versions`.`name` IN ("
    // + inClause.toString() + ") AND `verse_num` = ? ORDER BY `version`");
    //
    // st.setInt(1, position.getChapterNum());
    // st.setString(2, position.getBook().getName());
    //
    // for (int i = 3; i < batchSize + 3; i++)
    // st.setString(i, versions.get(versionNameToInsert++).getName());
    //
    // st.setInt(batchSize + 3, position.getVerseNum());
    //
    //
    // ResultSet rs = commitQuery(st);
    //
    // while (rs.next())
    // verseList.add(new Verse(rs.getString("text"), new Position(BibleBook
    // .getBibleBookByName(rs
    // .getString("book")), rs.getInt("chapter_num"), rs
    // .getInt("verse_num")), new BibleVersion(rs.getString("version"), rs
    // .getString("lang"))));
    //
    // } catch (SQLException e) {
    // throw new BibleStorageException("Verses could not be retrieved", e);
    // } finally {
    // try {
    // if (st != null)
    // st.close();
    // } catch (SQLException e) {
    // e.printStackTrace();
    // }
    // }
    // }
    //
    // return verseList;
    // }

    @Override
    public List<Verse> compareVerses(List<Position> positions, List<BibleVersion> versions)
	    throws BibleStorageException {
	ResultSet rs = null;
	PreparedStatement st = null;
	List<Verse> verseList = new ArrayList<Verse>();

	for (BibleVersion version : versions) {
	    for (Position position : positions) {
		try {
		    st = dbConnection
		    .prepareStatement("SELECT "+VERSE_TEXT_F+", "+VERSION_NAME_F+", "+VERSION_LANG_F+", "+COORD_VERSE_F+", "+COORD_CHAPT_F+", "+BOOK_NAME_F
					    + "FROM " + VERSIONS
					    + "INNER JOIN "+VERSES+" ON "+VERSE_VERSION_F+" = "+VERSION_ID_F+" "
					    + "INNER JOIN "+COORDS+" ON "+VERSE_COORD_F+" = "+COORD_ID_F+" "
					    + "INNER JOIN "+BOOKS+" ON "+COORD_BOOK_F+" = "+BOOK_ID_F+" "
					    + "WHERE "+COORD_CHAPT_F+" = ? AND "+BOOK_NAME_F+" = ? AND "+VERSION_NAME_F+" = ? AND "+COORD_VERSE_F+" = ? LIMIT 1");
		    st.setInt(1, position.getChapterNum());
		    st.setString(2, position.getBook().getName());
		    st.setString(3, version.getName());
		    st.setInt(4, position.getVerseNum());
		    rs = commitQuery(st);
		    while (rs.next())
			verseList.add(new Verse(rs.getString(1), new Position(BibleBook
				.getBibleBookByName(rs
					.getString(6)), rs.getInt(5), rs
				.getInt(4)), new BibleVersion(rs.getString(2), rs
				.getString(3))));

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
	}

	return verseList;
    }

}

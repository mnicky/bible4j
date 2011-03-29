package com.github.mnicky.bible4j.storage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.github.mnicky.bible4j.data.BibleBook;
import com.github.mnicky.bible4j.data.BibleVersion;
import com.github.mnicky.bible4j.data.Position;
import com.github.mnicky.bible4j.data.Verse;

public final class H2DbBibleStorage_Test {

    private Connection conn;
    private H2DbBibleStorage bible;

    @BeforeMethod
    public void setUpTest() {
	try {
	    conn = DriverManager.getConnection("jdbc:h2:mem:", "test", "");

	    // for debugging purposes:
	    // conn = DriverManager.getConnection("jdbc:h2:tcp://localhost/mem:test", "test", "");

	} catch (SQLException e) {
	    e.printStackTrace();
	    Assert.fail();
	}
	bible = new H2DbBibleStorage(conn);

    }

    @AfterMethod
    public void tearDownTest() {
	try {
	    conn.close();
	} catch (SQLException e) {
	    e.printStackTrace();
	    Assert.fail();
	}
    }

    @Test
    public void shouldCreateBibleStorage() {

	// expected numbers of column updates
	// see Statement.executeBatch() javadoc for more info
	int[] exp = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };

	int[] columns = null;

	try {
	    columns = bible.createStorage();
	} catch (Exception e) {
	    e.printStackTrace();
	    Assert.fail();
	}
	Assert.assertTrue(Arrays.equals(columns, exp));

    }

    @Test
    public void shouldCloseBibleStorage() {
	try {
	    bible.close();
	    Assert.assertTrue(conn.isClosed());
	} catch (Exception e) {
	    e.printStackTrace();
	    Assert.fail();
	}
    }

    @Test
    public void shouldInsertBibleBook() {

	Object[] exp = { "baruch", true };
	Object[] actual = new Object[2];

	try {
	    bible.createStorage();
	    bible.insertBibleBook(BibleBook.BARUCH);

	    Statement st = conn.createStatement();
	    ResultSet rs = st
		    .executeQuery("SELECT `name`, `is_deutero` FROM `bible_books` WHERE `name` = 'baruch' LIMIT 1");

	    int i = 0;
	    while (rs.next()) {
		actual[i++] = rs.getString("name");
		actual[i++] = rs.getBoolean("is_deutero");
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	    Assert.fail();
	}

	Assert.assertTrue(Arrays.deepEquals(actual, exp));

    }

    @Test
    public void shouldInsertPosition() {

	Object[] exp = { "john", 3, 16 };
	Object[] actual = new Object[3];

	try {
	    bible.createStorage();
	    bible.insertBibleBook(BibleBook.JOHN);
	    bible.insertPosition(new Position(BibleBook.JOHN, 3, 16));

	    Statement st = conn.createStatement();
	    ResultSet rs = st
		    .executeQuery("SELECT `name`, `chapter_num`, `verse_num` FROM `coords`, `bible_books` WHERE `bible_book_id` = `bible_books`.`id` LIMIT 1");

	    int i = 0;
	    while (rs.next()) {
		actual[i++] = rs.getString("name");
		actual[i++] = rs.getInt("chapter_num");
		actual[i++] = rs.getInt("verse_num");
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	    Assert.fail();
	}

	Assert.assertTrue(Arrays.deepEquals(actual, exp));

    }

    @Test
    public void shouldInsertBibleVersion() {

	Object[] exp = { "Douay-Rheims", "en" };
	Object[] actual = new Object[2];

	try {
	    bible.createStorage();
	    bible.insertBibleVersion(new BibleVersion("Douay-Rheims", "en"));

	    Statement st = conn.createStatement();
	    ResultSet rs = st
		    .executeQuery("SELECT `name`, `lang` FROM `bible_versions` WHERE `name` = 'Douay-Rheims' LIMIT 1");

	    int i = 0;
	    while (rs.next()) {
		actual[i++] = rs.getString("name");
		actual[i++] = rs.getString("lang");
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	    Assert.fail();
	}

	Assert.assertTrue(Arrays.deepEquals(actual, exp));

    }

    @Test
    public void shouldInsertVerse() {

	Object[] exp = { "There was a man sent from God, whose name was John.", "john", 1, 6,
		"English Standard Version", };
	Object[] actual = new Object[5];

	try {
	    bible.createStorage();
	    bible.insertBibleVersion(new BibleVersion("English Standard Version", "en"));
	    bible.insertBibleBook(BibleBook.JOHN);
	    bible.insertPosition(new Position(BibleBook.JOHN, 1, 6));
	    bible.insertVerse(new Verse("There was a man sent from God, whose name was John.", new Position(
		    BibleBook.JOHN, 1, 6), new BibleVersion("English Standard Version", "en")));

	    Statement st = conn.createStatement();
	    ResultSet rs = st
		    .executeQuery("SELECT `text`, `bible_books`.`name` AS `book`, `chapter_num`, `verse_num`, `bible_versions`.`name` AS `version` "
			    + "FROM `bible_versions` "
			    + "INNER JOIN `verses` ON `bible_versions`.`id` = `bible_version_id` "
			    + "INNER JOIN `coords` ON `coord_id` = `coords`.`id` "
			    + "INNER JOIN `bible_books` ON `bible_book_id` = `bible_books`.`id` "
			    + "WHERE `text` = 'There was a man sent from God, whose name was John.' LIMIT 1");

	    int i = 0;
	    while (rs.next()) {
		actual[i++] = rs.getString("text");
		actual[i++] = rs.getString("book");
		actual[i++] = rs.getInt("chapter_num");
		actual[i++] = rs.getInt("verse_num");
		actual[i++] = rs.getString("version");
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	    Assert.fail();
	}
	Assert.assertTrue(Arrays.deepEquals(actual, exp));
    }

    @Test
    public void testGetVerseWithOneVerse() {
	Verse exp = new Verse("test text", new Position(BibleBook.ACTS, 1, 2), new BibleVersion("KJV", "en"));
	Verse retrieved = null;

	try {
	    bible.createStorage();
	    bible.insertBibleVersion(new BibleVersion("KJV", "en"));
	    bible.insertBibleBook(BibleBook.ACTS);
	    bible.insertPosition(new Position(BibleBook.ACTS, 1, 2));
	    bible.insertVerse(new Verse("test text", new Position(BibleBook.ACTS, 1, 2), new BibleVersion(
		    "KJV", "en")));

	    retrieved = bible.getVerse(new Position(BibleBook.ACTS, 1, 2), new BibleVersion("KJV", "en"));

	} catch (Exception e) {
	    e.printStackTrace();
	    Assert.fail();
	}
	Assert.assertEquals(retrieved, exp);
    }

    @Test
    public void testGetVerseWithListOfVerses() {

	List<Verse> exp = new ArrayList<Verse>();
	exp.add(new Verse("test text1", new Position(BibleBook.ACTS, 1, 2), new BibleVersion("KJV", "en")));
	exp.add(new Verse("test text2", new Position(BibleBook.ACTS, 1, 3), new BibleVersion("KJV", "en")));
	exp.add(new Verse("test text3", new Position(BibleBook.ACTS, 1, 4), new BibleVersion("KJV", "en")));

	List<Verse> retrieved = null;

	try {
	    bible.createStorage();
	    bible.insertBibleVersion(new BibleVersion("KJV", "en"));
	    bible.insertBibleBook(BibleBook.ACTS);
	    bible.insertPosition(new Position(BibleBook.ACTS, 1, 2));
	    bible.insertPosition(new Position(BibleBook.ACTS, 1, 3));
	    bible.insertPosition(new Position(BibleBook.ACTS, 1, 4));
	    bible.insertVerse(new Verse("test text1", new Position(BibleBook.ACTS, 1, 2), new BibleVersion(
		    "KJV", "en")));
	    bible.insertVerse(new Verse("test text2", new Position(BibleBook.ACTS, 1, 3), new BibleVersion(
		    "KJV", "en")));
	    bible.insertVerse(new Verse("test text3", new Position(BibleBook.ACTS, 1, 4), new BibleVersion(
		    "KJV", "en")));

	    List<Position> positions = new ArrayList<Position>();
	    positions.add(new Position(BibleBook.ACTS, 1, 2));
	    positions.add(new Position(BibleBook.ACTS, 1, 3));
	    positions.add(new Position(BibleBook.ACTS, 1, 4));

	    retrieved = bible.getVerses(positions, new BibleVersion("KJV", "en"));

	} catch (Exception e) {
	    e.printStackTrace();
	    Assert.fail();
	}
	Assert.assertEquals(retrieved, exp);
    }

}

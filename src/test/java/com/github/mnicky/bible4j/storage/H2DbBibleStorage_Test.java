package com.github.mnicky.bible4j.storage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.github.mnicky.bible4j.data.BibleBook;
import com.github.mnicky.bible4j.data.Position;

public final class H2DbBibleStorage_Test {

    private Connection conn;
    private H2DbBibleStorage bible;

    @BeforeMethod
    public void setUpTest() {
	try {
	    conn = DriverManager.getConnection("jdbc:h2:mem:", "test", "");
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

	    Assert.assertTrue(Arrays.deepEquals(actual, exp));

	} catch (Exception e) {
	    e.printStackTrace();
	    Assert.fail();
	}

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

	    Assert.assertTrue(Arrays.deepEquals(actual, exp));

	} catch (Exception e) {
	    e.printStackTrace();
	    Assert.fail();
	}

    }

}

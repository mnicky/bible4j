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

public final class H2DbBibleStorage_Test {

    private Connection conn;
    private H2DbBibleStorage bible;

    @BeforeMethod
    public void setUpTest() {
	try {
	    conn = DriverManager.getConnection("jdbc:h2:mem:", "test", "");
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
	    ResultSet rs = st.executeQuery("SELECT name, is_deutero FROM bible_books LIMIT 1");
	    
	    int i = 0;
	    while(rs.next()) {
		actual[i++] = rs.getString(1);
		actual[i++] = rs.getBoolean(2);
	    }
	    
	    Assert.assertTrue(Arrays.deepEquals(actual, exp));

	} catch (Exception e) {
	    e.printStackTrace();
	    Assert.fail();
	}

    }

}

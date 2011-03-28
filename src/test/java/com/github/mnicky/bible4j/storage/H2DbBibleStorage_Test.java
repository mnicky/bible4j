package com.github.mnicky.bible4j.storage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

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

//    @Test
//    public void testCommitUpdateForCommit() {
//	int rows = 0;
//	try {
//	    Statement st = conn.createStatement();
//	    String sql = "CREATE TABLE IF NOT EXISTS verses (id IDENTITY NOT NULL , text " +
//	    		"VARCHAR(200) NOT NULL)";
//	    st.executeUpdate(sql);
//	    st.close();
//	    
//	    sql = "INSERT INTO vherses VALUES (1, 'ahoj')";
//	    st = conn.prepareStatement(sql);
//	    
//	    rows = bible.commitUpdate((PreparedStatement) st);
//	    
//	} catch(SQLException e) {
//	    e.printStackTrace();
//	    Assert.fail();
//	}
//	
//	Assert.assertEquals(rows, 1);
//
//    }

    @Test
    public void testClose() {
	try {
	    bible.close();
	    Assert.assertTrue(conn.isClosed());
	} catch (Exception e) {
	    Assert.fail();
	    // e.printStackTrace();
	}
    }

}

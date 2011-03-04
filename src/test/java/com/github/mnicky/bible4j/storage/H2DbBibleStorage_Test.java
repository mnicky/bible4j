package com.github.mnicky.bible4j.storage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;


public final class H2DbBibleStorage_Test {
    
    private Connection conn;
    private BibleStorage bible;
    
    @BeforeMethod
    public void setUpTest() {
	try {
	    conn = DriverManager.getConnection("jdbc:h2:mem:", "test", "");
	} catch (SQLException e) {
	    Assert.fail();
	    e.printStackTrace();
	}
	bible = new H2DbBibleStorage(conn);
	
    }
    
    @AfterMethod
    public void tearDownTest() {
	try {
	    conn.close();
	} catch (SQLException e) {
	    Assert.fail();
	    e.printStackTrace();
	}
    }
    
    @Test
    public void testClose() {
	try {
	    bible.close();
	    Assert.assertTrue(conn.isClosed());
	} catch (Exception e) {
	    Assert.fail();
	    e.printStackTrace();
	}
    }
    
}

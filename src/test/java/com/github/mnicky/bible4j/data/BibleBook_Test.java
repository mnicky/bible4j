package com.github.mnicky.bible4j.data;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.github.mnicky.bible4j.data.BibleBook.Testament;

public class BibleBook_Test {
    
    @Test
    public void testIsDeuterocaninicalForTrue() {
	Assert.assertTrue(BibleBook.BARUCH.isDeutero());	
    }
    
    @Test
    public void testIsDeuterocaninicalForFalse() {
	Assert.assertFalse(BibleBook.GALATIANS.isDeutero());
    }
    
    @Test
    public void testGetTestamentForOldTestament() {
	Assert.assertEquals(BibleBook.LEVITICUS.getTestament(), Testament.OLD);
    }
    
    @Test
    public void testGetTestamentForNewTestament() {
	Assert.assertEquals(BibleBook.LUKE.getTestament(), Testament.NEW);	
    }
    
    
}

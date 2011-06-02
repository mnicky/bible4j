package com.github.mnicky.bible4j.data;

import static org.testng.Assert.assertEquals;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Unit tests for Verse class.
 */
public final class Verse_Test {

    private Verse v1, v2, v3, v4, v5;

    @BeforeMethod
    public void beforeMethod() {
	v1 = new Verse("And how hear we every man in our own tongue, wherein we were born?", new Position(BibleBook.ACTS, 2, 8), new BibleVersion("King's James Version", "KJV", "en"));
	v2 = new Verse("But this is that which was spoken by the prophet Joel;", new Position(BibleBook.ACTS, 2, 16), new BibleVersion("King's James Version", "KJV", "en"));
	v3 = new Verse("But this is that which was spoken by the prophet Joel;", new Position(BibleBook.ACTS, 2, 16), new BibleVersion("King's James Version", "KJV", "en"));
	v4 = new Verse(null, null, null);
	v5 = new Verse(null, null, null);
    }

    @Test
    public void testToString() {
	String exp = "But this is that which was spoken by the prophet Joel; - ACTS 2,16; King's James Version (KJV, en)";
	String tested = v2.toString();
	assertEquals(tested, exp);
    }
    
    //TODO add more equals() tests

    @Test
    public void testEqualsForTrue() {
	boolean exp = true;
	boolean act = v3.equals(v2);
	assertEquals(act, exp);
    }

    @Test
    public void testEqualsForFalse() {
	boolean exp = false;
	boolean act = v1.equals(v2);
	assertEquals(act, exp);
    }
    
    @Test
    public void testEqualsForVerseContainingNullExpectingTrue() {
	boolean exp = true;
	boolean act = v4.equals(v5);
	assertEquals(act, exp);
    }
    
    @Test
    public void testEqualsForVerseContainingNullExpectingFalse() {
	boolean exp = false;
	boolean act = v1.equals(v5);
	assertEquals(act, exp);
    }
    
    @Test
    public void testHashCodeForConsistency() {
        int h3 = v2.hashCode();
        int h4 = v3.hashCode();
        Assert.assertEquals(h3 == h4, true);
    }

    @Test
    public void testHashCodeForConsistencyWithEqualsForTrue() {
	int h3 = v2.hashCode();
	int h4 = v3.hashCode();
	boolean b = v2.equals(v3);
	Assert.assertEquals(h3 == h4, b);
    }

    @Test
    public void testHashCodeForConsistencyWithEqualsForFalse() {
	int h2 = v1.hashCode();
	int h4 = v3.hashCode();
	boolean b = v1.equals(v3);
	Assert.assertEquals(h2 == h4, b);
    }
    
    @Test
    public void shouldCompareVersesByPositions() {
	assertEquals(v1.compareTo(v2), -1);
	assertEquals(v2.compareTo(v1), 1);
	assertEquals(v1.compareTo(v1), 0);
    }

}

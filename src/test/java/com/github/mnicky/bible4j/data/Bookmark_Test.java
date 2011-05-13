package com.github.mnicky.bible4j.data;

import static org.testng.Assert.assertEquals;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.github.mnicky.bible4j.data.BibleBook;
import com.github.mnicky.bible4j.data.Bookmark;
import com.github.mnicky.bible4j.data.Position;

/**
 * Unit tests for Bookmark class.
 */
public final class Bookmark_Test {

    private Bookmark b2, b3, b4;

    @BeforeMethod
    public void beforeMethod() {
	b2 = new Bookmark("understanding",
			  new Verse("And how hear we every man in our own tongue, wherein we were born?",
				    new Position(BibleBook.ACTS, 2, 8), new BibleVersion("KJV", "en")));
	b3 = new Bookmark("joel", new Verse("But this is that which was spoken by the prophet Joel;",
					     new Position(BibleBook.ACTS, 2, 16), new BibleVersion("KJV",
												   "en")));
	b4 = new Bookmark("joel", new Verse("But this is that which was spoken by the prophet Joel;",
					     new Position(BibleBook.ACTS, 2, 16), new BibleVersion("KJV",
												   "en")));

    }

    @Test
    public void testToString() {
	String exp = "joel: "
		+ "But this is that which was spoken by the prophet Joel; - ACTS 2,16;  (KJV, en)";

	String tested = b3.toString();
	assertEquals(tested, exp);

    }
    
    //TODO add more equals() tests

    @Test
    public void testEqualsForTrue() {
	boolean exp = true;

	boolean act = b4.equals(b3);

	assertEquals(act, exp);
    }

    @Test
    public void testEqualsForFalse() {
	boolean exp = false;

	boolean act = b2.equals(b3);

	assertEquals(act, exp);
    }
    
    @Test
    public void testHashCodeForConsistency() {
        int h3 = b3.hashCode();
        int h4 = b4.hashCode();
        Assert.assertEquals(h3 == h4, true);
    }

    @Test
    public void testHashCodeForConsistencyWithEqualsForTrue() {
	int h3 = b3.hashCode();
	int h4 = b4.hashCode();
	boolean b = b3.equals(b4);
	Assert.assertEquals(h3 == h4, b);
    }

    @Test
    public void testHashCodeForConsistencyWithEqualsForFalse() {
	int h2 = b2.hashCode();
	int h4 = b4.hashCode();
	boolean b = b2.equals(b4);
	Assert.assertEquals(h2 == h4, b);
    }
    
    @Test
    public void shouldCompareBookmarksByPositionsOfTheirVerses() {
	assertEquals(b2.compareTo(b3), -1);
	assertEquals(b3.compareTo(b2), 1);
	assertEquals(b2.compareTo(b2), 0);
    }

}

package com.github.mnicky.bible4j.data;

import static org.testng.Assert.assertEquals;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.github.mnicky.bible4j.data.BibleBook;
import com.github.mnicky.bible4j.data.Bookmark;
import com.github.mnicky.bible4j.data.Note.NoteType;
import com.github.mnicky.bible4j.data.Position;

public final class Note_Test {

    private Note n1, n2, n3;

    @BeforeMethod
    public void beforeMethod() {
	n1 = new Note("some note text", new Position(BibleBook.ACTS, 2, 8), NoteType.COMMENTARY);
	n2 = new Note("some note text", new Position(BibleBook.ACTS, 2, 8), NoteType.USER_NOTE);
	n3 = new Note("some note text", new Position(BibleBook.ACTS, 2, 8), NoteType.USER_NOTE);

    }

    @Test
    public void testToString() {
	String exp = "some note text - ACTS 2,8 (COMMENTARY)";

	String tested = n1.toString();
	assertEquals(tested, exp);

    }
    
    //TODO add more equals() tests

    @Test
    public void testEqualsForTrue() {
	boolean exp = true;

	boolean act = n3.equals(n2);

	assertEquals(act, exp);
    }

    @Test
    public void testEqualsForFalse() {
	boolean exp = false;

	boolean act = n1.equals(n2);

	assertEquals(act, exp);
    }
    
    @Test
    public void testHashCodeForConsistency() {
        int h2 = n2.hashCode();
        int h3 = n3.hashCode();
        Assert.assertEquals(h2 == h3, true);
    }

    @Test
    public void testHashCodeForConsistencyWithEqualsForTrue() {
	int h2 = n2.hashCode();
	int h3 = n3.hashCode();
	boolean b = n2.equals(n3);
	Assert.assertEquals(h2 == h3, b);
    }

    @Test
    public void testHashCodeForConsistencyWithEqualsForFalse() {
	int h1 = n1.hashCode();
	int h3 = n3.hashCode();
	boolean b = n1.equals(n3);
	Assert.assertEquals(h1 == h3, b);
    }

}

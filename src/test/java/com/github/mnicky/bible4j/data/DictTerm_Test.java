package com.github.mnicky.bible4j.data;

import static org.testng.Assert.assertEquals;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.github.mnicky.bible4j.data.BibleBook;
import com.github.mnicky.bible4j.data.Note.NoteType;
import com.github.mnicky.bible4j.data.Position;

public final class DictTerm_Test {

    private DictTerm t1, t2, t3;

    @BeforeMethod
    public void beforeMethod() {
	t1 = new DictTerm("name1", "def1");
	t2 = new DictTerm("name2", "def2");
	t3 = new DictTerm("name2", "def2");

    }

    @Test
    public void testToString() {
	String exp = "name1 - def1";

	String tested = t1.toString();
	assertEquals(tested, exp);

    }
    
    //TODO add more equals() tests

    @Test
    public void testEqualsForTrue() {
	boolean exp = true;

	boolean act = t3.equals(t2);

	assertEquals(act, exp);
    }

    @Test
    public void testEqualsForFalse() {
	boolean exp = false;

	boolean act = t1.equals(t2);

	assertEquals(act, exp);
    }
    
    @Test
    public void testHashCodeForConsistency() {
        int h2 = t2.hashCode();
        int h3 = t3.hashCode();
        Assert.assertEquals(h2 == h3, true);
    }

    @Test
    public void testHashCodeForConsistencyWithEqualsForTrue() {
	int h2 = t2.hashCode();
	int h3 = t3.hashCode();
	boolean b = t2.equals(t3);
	Assert.assertEquals(h2 == h3, b);
    }

    @Test
    public void testHashCodeForConsistencyWithEqualsForFalse() {
	int h1 = t1.hashCode();
	int h3 = t3.hashCode();
	boolean b = t1.equals(t3);
	Assert.assertEquals(h1 == h3, b);
    }

}

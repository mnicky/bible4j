package com.github.mnicky.bible4j.data;

import static org.testng.Assert.assertEquals;
import hirondelle.date4j.DateTime;

import java.util.ArrayList;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public final class DailyReading_Test {

    private DailyReading r1, r2, r3;

    @BeforeMethod
    public void beforeMethod() {
	r1 = new DailyReading("list1", new DateTime("2010-05-12"), new ArrayList<Position>());
	r2 = new DailyReading("list2", new DateTime("2010-05-06"), new ArrayList<Position>());
	r3 = new DailyReading("list2", new DateTime("2010-05-06"), new ArrayList<Position>());

    }

    @Test
    public void testToString() {
	String exp = "list1 - 12-05-2010";

	String tested = r1.toString();
	assertEquals(tested, exp);

    }
    
    //TODO add more equals() tests

    @Test
    public void testEqualsForTrue() {
	boolean exp = true;

	boolean act = r3.equals(r2);

	assertEquals(act, exp);
    }

    @Test
    public void testEqualsForFalse() {
	boolean exp = false;

	boolean act = r1.equals(r2);

	assertEquals(act, exp);
    }
    
    @Test
    public void testHashCodeForConsistency() {
        int h2 = r2.hashCode();
        int h3 = r3.hashCode();
        Assert.assertEquals(h2 == h3, true);
    }

    @Test
    public void testHashCodeForConsistencyWithEqualsForTrue() {
	int h2 = r2.hashCode();
	int h3 = r3.hashCode();
	boolean b = r2.equals(r3);
	Assert.assertEquals(h2 == h3, b);
    }

    @Test
    public void testHashCodeForConsistencyWithEqualsForFalse() {
	int h1 = r1.hashCode();
	int h3 = r3.hashCode();
	boolean b = r1.equals(r3);
	Assert.assertEquals(h1 == h3, b);
    }

}

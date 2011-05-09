package com.github.mnicky.bible4j.data;

import static org.testng.Assert.assertEquals;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Unit tests for Position class.
 */
public class Position_Test {
    
    private Position pos1, pos2, pos3, pos4; 

    @BeforeMethod
    public void beforeMethod() {
        pos1 = new Position(BibleBook.MATTHEW, 6, 14);
        pos2 = new Position(BibleBook.MATTHEW, 6, 14);
        pos3 = new Position(BibleBook.MATTHEW, 6, 14);
        pos4 = new Position(BibleBook.JOHN, 5, 11);
    }
    
    @Test
    public void testToString() {
        String s = pos1.toString();
        Assert.assertEquals(s, "MATTHEW 6,14");
    }
    
    @Test
    public void testEqualsForReflexivity() {
        boolean b = pos1.equals(pos1);
        Assert.assertEquals(b, true);
    }
    
    @Test
    public void testEqualsForSymmetryForTrue() {
        boolean b = pos1.equals(pos2) && pos2.equals(pos1);
        Assert.assertEquals(b, true);
    }
    
    @Test
    public void testEqualsForSymmetryForFalse() {
        boolean b = pos1.equals(pos4) || pos4.equals(pos1);
        Assert.assertEquals(b, false);
    }
    
    @Test
    public void testEqualsForTransitivity() {
        boolean b1 = pos1.equals(pos2) && pos2.equals(pos3);
        boolean b2 = pos1.equals(pos3);
        Assert.assertEquals(b2 && b1, true);
    }
    
    @Test
    public void testEqualsForConsistency() {
        boolean b1 = pos1.equals(pos4);
        boolean b2 = pos1.equals(pos4);
        Assert.assertEquals(b1 && b2, false);
    }
   
   
    @Test
    public void testHashCodeForConsistency() {
        int h1 = pos1.hashCode();
        int h2 = pos1.hashCode();
        Assert.assertEquals(h1 == h2, true);
    }
    
    @Test
    public void testHashCodeForConsistencyWithEqualsForTrue() {
        int h1 = pos1.hashCode();
        int h2 = pos2.hashCode();
        boolean b = pos1.equals(pos2);
        Assert.assertEquals(h1 == h2, b);
    }
    
    @Test
    public void testHashCodeForConsistencyWithEqualsForFalse() {
        int h1 = pos1.hashCode();
        int h2 = pos4.hashCode();
        boolean b = pos1.equals(pos4);
        Assert.assertEquals(h1 == h2, b);
    }
    
    @Test
    public void testCompareTo() {
	compTestHelp(1, new Position(BibleBook.ACTS, 1, 2), new Position(BibleBook.ACTS, 1, 2), 0);
	compTestHelp(2, new Position(BibleBook.ACTS, 1, 2), new Position(BibleBook.ACTS, 1, 1), 1);
	compTestHelp(3, new Position(BibleBook.ACTS, 2, 2), new Position(BibleBook.ACTS, 1, 2), 1);
	compTestHelp(4, new Position(BibleBook.ACTS, 1, 2), new Position(BibleBook.LUKE, 1, 2), 1);
	compTestHelp(5, new Position(BibleBook.ACTS, 1, 1), new Position(BibleBook.ACTS, 1, 2), -1);
	compTestHelp(6, new Position(BibleBook.ACTS, 1, 2), new Position(BibleBook.ACTS, 2, 2), -1);
	compTestHelp(7, new Position(BibleBook.JOHN, 1, 2), new Position(BibleBook.ACTS, 1, 2), -1);
    }
    
    public void compTestHelp(int n, Position p1, Position p2, int exp) {
	assertEquals(p1.compareTo(p2), exp, "call no. " + n + " failed");
    }
    
}

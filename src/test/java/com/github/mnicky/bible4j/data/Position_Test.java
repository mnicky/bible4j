package com.github.mnicky.bible4j.data;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

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
    public void testEqualsForNull() { 
        boolean b = pos1.equals(null);
        Assert.assertEquals(b, false);
    }
    
    @Test
    public void testEqualsWithAnotherType() {
        String s = new String();
        boolean b = pos1.equals(s);
        Assert.assertEquals(b, false);
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
    
}

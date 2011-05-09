package com.github.mnicky.bible4j.data;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Unit tests for BibleVersion class.
 */
public class BibleVersion_Test {
    
    private BibleVersion bv1, bv2, bv3, bv4; 

    @BeforeMethod
    public void beforeMethod() {
        bv1 = new BibleVersion("King's James Version", "KJV", "en");
        bv2 = new BibleVersion("King's James Version", "KJV", "en");
        bv3 = new BibleVersion("King's James Version", "KJV", "en");
        bv4 = new BibleVersion("Cesky ekumenicky preklad", "CEP", "cz");
    }
    
    @Test
    public void testToString() {
        String s = bv1.toString();
        Assert.assertEquals(s, "King's James Version (KJV, en)");
    }
    
    @Test
    public void testEqualsForReflexivity() {
        boolean b = bv1.equals(bv1);
        Assert.assertEquals(b, true);
    }
    
    @Test
    public void testEqualsForSymmetryForTrue() {
        boolean b = bv1.equals(bv2) && bv2.equals(bv1);
        Assert.assertEquals(b, true);
    }
    
    @Test
    public void testEqualsForSymmetryForFalse() {
        boolean b = bv1.equals(bv4) || bv4.equals(bv1);
        Assert.assertEquals(b, false);
    }
    
    @Test
    public void testEqualsForTransitivity() {
        boolean b1 = bv1.equals(bv2) && bv2.equals(bv3);
        boolean b2 = bv1.equals(bv3);
        Assert.assertEquals(b2 && b1, true);
    }
    
    @Test
    public void testEqualsForConsistency() {
        boolean b1 = bv1.equals(bv4);
        boolean b2 = bv1.equals(bv4);
        Assert.assertEquals(b1 && b2, false);
    }
   
   
    @Test
    public void testHashCodeForConsistency() {
        int h1 = bv1.hashCode();
        int h2 = bv1.hashCode();
        Assert.assertEquals(h1 == h2, true);
    }
    
    @Test
    public void testHashCodeForConsistencyWithEqualsForTrue() {
        int h1 = bv1.hashCode();
        int h2 = bv2.hashCode();
        boolean b = bv1.equals(bv2);
        Assert.assertEquals(h1 == h2, b);
    }
    
    @Test
    public void testHashCodeForConsistencyWithEqualsForFalse() {
        int h1 = bv1.hashCode();
        int h2 = bv4.hashCode();
        boolean b = bv1.equals(bv4);
        Assert.assertEquals(h1 == h2, b);
    }
    
}

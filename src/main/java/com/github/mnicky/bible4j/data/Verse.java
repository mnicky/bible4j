package com.github.mnicky.bible4j.data;

/**
 * Represents one Bible verse.
 */
public final class Verse {

    /**
     * Text of this Verse.
     */
    private final String text;
    
    /**
     * Position of this Verse in the Bible.
     */
    private final Position position;
    
    /**
     * Version of the Bible this verse is in.
     */
    private final BibleVersion bibleVersion;
    

    /**
     * Constructs new Verse with specified text, position and Bible version.
     * @param text text of the verse
     * @param position position of the verse in the Bible (coordinates)
     * @param bibleVersion version of the Bible this verse is from
     */
    public Verse(String text, Position position, BibleVersion bibleVersion) {
	this.text = text;
	this.position = position;
	this.bibleVersion = bibleVersion;
    }
    
}

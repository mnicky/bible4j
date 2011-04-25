package com.github.mnicky.bible4j.data;

/**
 * Represents one Bible verse.
 */
public final class Verse implements Comparable<Verse> {

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
     * 
     * @param text
     *            text of the verse
     * @param position
     *            position of the verse in the Bible (coordinates)
     * @param bibleVersion
     *            version of the Bible this verse is from
     */
    public Verse(String text, Position position, BibleVersion bibleVersion) {
	this.text = text;
	this.position = position;
	this.bibleVersion = bibleVersion;
    }

    public String getText() {
	return text;
    }

    public Position getPosition() {
	return position;
    }

    public BibleVersion getBibleVersion() {
	return bibleVersion;
    }

    /**
     * Returns the string representation of this Verse. The representation format is subject to
     * change, but the following may be regarded as typical:
     * 
     * "My spirit has rejoiced in God my Savior - LUKE 1,47; The World English Bible (en)"
     * 
     * @return string representation of this Verse
     */
    @Override
    public String toString() {
	return text + " - " + position.toString() + "; " + bibleVersion.toString();
    }

    /**
     * Indicates whether the provided object equals to this Verse object.
     * 
     * @param obj
     *            object to compare this Verse object with
     * @return true if this Verse object equals to provided
     */
    @Override
    public boolean equals(Object obj) {
	if (obj == this)
	    return true;
	if (!(obj instanceof Verse))
	    return false;
	Verse verse = (Verse) obj;
	return verse.text.equals(this.text) && verse.position.equals(this.position)
		&& verse.bibleVersion.equals(this.bibleVersion);
    }

    /**
     * Returns a hash code for this Verse.
     * 
     * @return a hash code for this Verse
     */
    @Override
    public int hashCode() {
	int result = 17;
	result = 31 * result + (text == null ? 0 : text.hashCode());
	result = 31 * result + (position == null ? 0 : position.hashCode());
	result = 31 * result + (bibleVersion == null ? 0 : bibleVersion.hashCode());
	return result;
    }

    @Override
    public int compareTo(Verse v) {
	return this.position.compareTo(v.getPosition());
    }
    
    //TODO add Verse unit test

}

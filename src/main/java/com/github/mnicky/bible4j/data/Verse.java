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

    @Override
    public int compareTo(Verse v) {
	return this.position.compareTo(v.getPosition());
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (!(obj instanceof Verse))
	    return false;
	Verse other = (Verse) obj;
	if (this.bibleVersion == null) {
	    if (other.bibleVersion != null)
		return false;
	}
	else if (!this.bibleVersion.equals(other.bibleVersion))
	    return false;
	if (this.position == null) {
	    if (other.position != null)
		return false;
	}
	else if (!this.position.equals(other.position))
	    return false;
	if (this.text == null) {
	    if (other.text != null)
		return false;
	}
	else if (!this.text.equals(other.text))
	    return false;
	return true;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((this.bibleVersion == null) ? 0 : this.bibleVersion.hashCode());
	result = prime * result + ((this.position == null) ? 0 : this.position.hashCode());
	result = prime * result + ((this.text == null) ? 0 : this.text.hashCode());
	return result;
    }

}

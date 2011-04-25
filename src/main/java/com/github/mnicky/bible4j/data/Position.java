package com.github.mnicky.bible4j.data;


/**
 * Class representing position (coordinates) in the Bible.
 */
public final class Position implements Comparable<Position> {
    
    /**
     * Bible Book.
     */
    private final BibleBook book;
    
    /**
     * Chapter number.
     */
    private final int chapterNum;
    
    /**
     * Verse number.
     */
    private final int verseNum;
    
    
    /**
     * Constructs new Position with specified Bible book, chapter number and verse number.
     * @param book bible book
     * @param chapterNum chapter number
     * @param verseNum verse number
     */
    public Position(BibleBook book, int chapterNum, int verseNum) {
	this.book = book;
	this.chapterNum = chapterNum;
	this.verseNum = verseNum;
    }
    
    
    /**
     * Returns the string representation of this Position.
     * The representation format is subject to change,
     * but the following may be regarded as typical:
     *
     * "MATTHEW 6,14"
     *
     * @return string representation of this Position
     */
    @Override
    public String toString() {
	return book + " " + chapterNum + "," + verseNum;
    }
    
    
    /**
     * Indicates whether the provided object equals to this Position object.
     * @param obj object to compare this Position object with     *
     * @return true if this Position object equals to provided
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (!(obj instanceof Position))
            return false;
        Position pos = (Position) obj;
        return pos.book == this.book 
         && pos.chapterNum == this.chapterNum && pos.verseNum == this.verseNum;
    }
    
    /**
     * Returns a hash code for this Position.     * 
     * @return a hash code for this Position
     */
    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + (book == null ? 0 : book.hashCode());
        result = 31 * result + (Integer.valueOf(chapterNum).hashCode());
        result = 31 * result + (Integer.valueOf(verseNum).hashCode());
        return result;
    }
    
    //TODO implements comparable on Position 


    public BibleBook getBook() {
	return book;
    }


    public int getChapterNum() {
	return chapterNum;
    }


    public int getVerseNum() {
	return verseNum;
    }

    @Override
    public int compareTo(Position p) {
	
	if (this.book.ordinal() > p.book.ordinal())
	    return 1;
	
	else if (this.book.ordinal() == p.book.ordinal()) {
	    if (this.chapterNum > p.chapterNum)
		return 1;
	    else if (this.chapterNum == p.chapterNum) {
		if (this.verseNum > p.verseNum)
		    return 1;
		else if (this.verseNum == p.verseNum)
		    return 0;
		else return -1;
	    }
	    else
		return -1;
	}
	else
	    return -1;
    }

}

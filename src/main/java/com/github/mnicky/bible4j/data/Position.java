package com.github.mnicky.bible4j.data;


/**
 * Class representing position (coordinates) in the Bible. If verse number is not set, it represents the whole chapter.
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
    
    
    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (!(obj instanceof Position))
	    return false;
	Position other = (Position) obj;
	if (this.book != other.book)
	    return false;
	if (this.chapterNum != other.chapterNum)
	    return false;
	if (this.verseNum != other.verseNum)
	    return false;
	return true;
    }
    
    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((this.book == null) ? 0 : this.book.hashCode());
	result = prime * result + this.chapterNum;
	result = prime * result + this.verseNum;
	return result;
    }


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

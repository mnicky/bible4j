package com.github.mnicky.bible4j.data;

/**
 * Represents the Bookmark.
 */
public final class Bookmark implements Comparable<Bookmark> {
    
    private final String name;
    
    private final Verse verse;
    
    public Bookmark(String name , Verse verse) {
	this.name = name;
	this.verse = verse;
    }

    public String getName() {
	return name;
    }

    public Verse getVerse() {
	return verse;
    }
    
    @Override
    public String toString() {
	return name + ": " + verse.toString();
    }
    
    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (!(obj instanceof Bookmark))
	    return false;
	Bookmark other = (Bookmark) obj;
	if (this.name == null) {
	    if (other.name != null)
		return false;
	}
	else if (!this.name.equals(other.name))
	    return false;
	if (this.verse == null) {
	    if (other.verse != null)
		return false;
	}
	else if (!this.verse.equals(other.verse))
	    return false;
	return true;
    }
    
    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((this.name == null) ? 0 : this.name.hashCode());
	result = prime * result + ((this.verse == null) ? 0 : this.verse.hashCode());
	return result;
    }

    @Override
    public int compareTo(Bookmark b) {
	return this.verse.getPosition().compareTo(b.getVerse().getPosition());
    }

}

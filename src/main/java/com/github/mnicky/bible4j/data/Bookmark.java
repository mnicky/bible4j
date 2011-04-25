package com.github.mnicky.bible4j.data;

public final class Bookmark {
    
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
        if (obj == this)
            return true;
        if (!(obj instanceof Bookmark))
            return false;
        Bookmark b = (Bookmark) obj;
        return b.name.equals(this.name) && b.verse.equals(this.verse);
    }
    
    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + (name == null ? 0 : name.hashCode());
        result = 31 * result + (verse == null ? 0 : verse.hashCode());
        return result;
    }

}

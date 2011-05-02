package com.github.mnicky.bible4j.data;

/**
 * Class representing version of the Bible.
 */
public final class BibleVersion {

    /**
     * Abbreviation of this Bible version. 
     */
    private final String abbr;
    
    /**
     * Language of this Bible version.
     */
    private final String language;
    
    /**
     * Name of this Bible version.
     */
    private final String name; 
    
    /**
     * Constructs new BibleVersion with specified abbreviation and language.
     * @param abbr abbreviation of the Bible version
     * @param language language
     */
    public BibleVersion(String abbr, String language) {
	this.abbr = abbr;
	this.language = language;
	this.name = "";
    }
    
    /**
     * Constructs new BibleVersion with specified name, abbreviation and language.
     * @param name name of the Bible version
     * @param abbr abbreviation of the Bible version
     * @param language language
     */
    public BibleVersion(String name, String abbr, String language) {
	this.abbr = abbr;
	this.language = language;
	this.name = name;
    }
    
    
    /**
     * Returns the string representation of this BibleVersion.
     * The representation format is subject to change,
     * but the following may be regarded as typical:
     *
     * "King's James Version (KJV, en)"
     *
     * @return string representation of this BibleVersion
     */
    @Override
    public String toString() {
	return name + " (" + abbr + ", " + language + ")";
    }
    
    /**
     * Indicates whether the provided object equals to this BibleVersion object.
     * @param obj object to compare this BibleVersion object with 
     * @return true if this BibleVersion object equals to provided
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (!(obj instanceof BibleVersion))
            return false;
        BibleVersion bv = (BibleVersion) obj;
        return bv.abbr.equals(this.abbr) && bv.language.equals(this.language) && bv.name.equals(this.name);
    }
    
    /**
     * Returns a hash code for this Position.
     * @return a hash code for this Position
     */
    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + (abbr == null ? 0 : abbr.hashCode());
        result = 31 * result + (language == null ? 0 : language.hashCode());
        result = 31 * result + (name == null ? 0 : name.hashCode());
        return result;
    }


    public String getAbbr() {
	return abbr;
    }


    public String getLanguage() {
	return language;
    }   
    
    public String getName() {
	return name;
    } 
    
}

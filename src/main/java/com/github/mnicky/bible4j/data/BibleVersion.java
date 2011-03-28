package com.github.mnicky.bible4j.data;

/**
 * Class representing version of the Bible.
 */
public final class BibleVersion {

    /**
     * Name of this Bible version. 
     */
    private final String name;
    
    /**
     * Language of this Bible version.
     */
    private final String language;
    
    //TODO add bible version abbreviation?
    
    /**
     * Constructs new BibleVersion with specified name and language.
     * @param name name of the Bible version
     * @param language language
     */
    public BibleVersion(String name, String language) {
	this.name = name;
	this.language = language;
    }
    
    
    /**
     * Returns the string representation of this BibleVersion.
     * The representation format is subject to change,
     * but the following may be regarded as typical:
     *
     * "King's James Version (en)"
     *
     * @return string representation of this BibleVersion
     */
    @Override
    public String toString() {
	return name + " (" + language + ")";
    }
    
    /**
     * Indicates whether the provided object equals to this BibleVersion object.
     * @param obj object to compare this BibleVersion object with     *
     * @return true if this BibleVersion object equals to provided
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (!(obj instanceof BibleVersion))
            return false;
        BibleVersion bv = (BibleVersion) obj;
        return bv.name.equals(this.name) && bv.language.equals(this.language);
    }
    
    /**
     * Returns a hash code for this Position.     * 
     * @return a hash code for this Position
     */
    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + (name == null ? 0 : name.hashCode());
        result = 31 * result + (language == null ? 0 : language.hashCode());
        return result;
    }


    public String getName() {
	return name;
    }


    public String getLanguage() {
	return language;
    }   
    
}

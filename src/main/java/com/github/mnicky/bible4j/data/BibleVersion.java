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
    
    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (!(obj instanceof BibleVersion))
	    return false;
	BibleVersion other = (BibleVersion) obj;
	if (this.abbr == null) {
	    if (other.abbr != null)
		return false;
	}
	else if (!this.abbr.equals(other.abbr))
	    return false;
	if (this.language == null) {
	    if (other.language != null)
		return false;
	}
	else if (!this.language.equals(other.language))
	    return false;
	if (this.name == null) {
	    if (other.name != null)
		return false;
	}
	else if (!this.name.equals(other.name))
	    return false;
	return true;
    }
    
    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((this.abbr == null) ? 0 : this.abbr.hashCode());
	result = prime * result + ((this.language == null) ? 0 : this.language.hashCode());
	result = prime * result + ((this.name == null) ? 0 : this.name.hashCode());
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

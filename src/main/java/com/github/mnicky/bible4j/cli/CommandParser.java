package com.github.mnicky.bible4j.cli;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import com.github.mnicky.bible4j.data.BibleVersion;
import com.github.mnicky.bible4j.storage.BibleStorage;
import com.github.mnicky.bible4j.storage.BibleStorageException;

public abstract class CommandParser {
    
    protected static final String BIBLE_VERSION_ARGUMENT = "-v";
    protected static final String BIBLE_BOOK_ARGUMENT = "-b";
    
    protected final BibleStorage bibleStorage;
    
    public CommandParser(BibleStorage bibleStorage) {
	this.bibleStorage = bibleStorage;
    }

    public abstract void parse(String[] args) throws BibleStorageException;
    
    abstract public void printHelp();
    
    protected boolean isArgumentPresent(String arg, String[] args) {
	
	for (String a : args)
	    if (a.equalsIgnoreCase(arg))
	    	return true;
	
	return false;
    }
    
    /**
     * Returns true if the word is argument (not a value). The word is argument if it starts with character '-'.<br><br>
     * E. g.: program -arg value1 --arg2 value2 -a3 value3 value4
     */
    private boolean isArgument(String word) {
	return word.startsWith("-");
    }
    
    protected String getFirstValue(String[] args) {
	if (isArgument(args[0]))
	    throw new IllegalArgumentException("The first word is an argument, not a value.");
	return args[0];
    }
    
    protected List<String> getAllNonArgumentValues(String[] args) {
	List<String> values = new ArrayList<String>();
	
	for (String value : args) {
	    if (isArgument(value))
		break;
	    values.add(value);
	}
	
	return values;
    }
    
    protected String getFirstValueOfArgument(String arg, String[] args) {
	for (int i = 0; i < args.length; i++)
	    if (args[i].equalsIgnoreCase(arg) && (i + 1) < args.length)
	    	return args[i + 1];
	
	throw new IllegalArgumentException("Argument " + arg + " not present or without value.");
    }
    
    private int getArgumentIndex(String arg, String[] args) {
	for (int i = 0; i < args.length; i++)
	    if (args[i].equalsIgnoreCase(arg))
		return i;

	throw new IllegalArgumentException("Argument " + arg + " not present.");
    }
    
    protected List<String> getAllValuesOfArgument(String arg, String[] args) {
	int argPosition = getArgumentIndex(arg, args);
	List<String> argValues = new ArrayList<String>();
	
	for (int i = argPosition + 1; i < args.length && !isArgument(args[i]); i++) {
	    argValues.add(args[i]);
	}
	return argValues;
    }

    protected List<BibleVersion> parseVersionsAndReturnFirstIfEmpty(String[] args) throws BibleStorageException {
        List<BibleVersion> versionList = new ArrayList<BibleVersion>();
        if (isArgumentPresent(BIBLE_VERSION_ARGUMENT, args)) {
            for (String version : getAllValuesOfArgument(BIBLE_VERSION_ARGUMENT, args))
        	versionList.add(bibleStorage.getBibleVersion(version));
        }
        else 
            versionList.add(bibleStorage.getAllBibleVersions().get(0));
        return versionList;
    }
    
    protected List<BibleVersion> parseVersionsAndReturnAllIfEmpty(String[] args) throws BibleStorageException {
        List<BibleVersion> versionList = new ArrayList<BibleVersion>();
        if (isArgumentPresent(BIBLE_VERSION_ARGUMENT, args)) {
            for (String version : getAllValuesOfArgument(BIBLE_VERSION_ARGUMENT, args))
        	versionList.add(bibleStorage.getBibleVersion(version));
        }
        else 
            versionList = (bibleStorage.getAllBibleVersions());
        return versionList;
    }
    
    protected List<BibleVersion> parseVersionsAndReturnNoneIfEmpty(String[] args) throws BibleStorageException {
        List<BibleVersion> versionList = new ArrayList<BibleVersion>();
        if (isArgumentPresent(BIBLE_VERSION_ARGUMENT, args)) {
            for (String version : getAllValuesOfArgument(BIBLE_VERSION_ARGUMENT, args))
        	versionList.add(bibleStorage.getBibleVersion(version));
        }
        return versionList;
    }
    
    

}

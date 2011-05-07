package com.github.mnicky.bible4j.cli;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.github.mnicky.bible4j.data.BibleVersion;
import com.github.mnicky.bible4j.parsers.BibleExporterException;
import com.github.mnicky.bible4j.parsers.BibleImporterException;
import com.github.mnicky.bible4j.storage.BibleStorage;
import com.github.mnicky.bible4j.storage.BibleStorageException;

public abstract class CommandRunner {
    
    protected static final String BIBLE_VERSION_ARGUMENT = "-v";
    protected static final String BIBLE_BOOK_ARGUMENT = "-b";
    protected static final String ADD_ARGUMENT = "-add";
    protected static final String DOWNLOAD_ARGUMENT = "-down";
    
    protected final BibleStorage bibleStorage;
    
    public CommandRunner(BibleStorage bibleStorage) {
	this.bibleStorage = bibleStorage;
    }

    abstract void parseCommandLine(String[] args) throws BibleStorageException, BibleImporterException, BibleExporterException, IOException;
    
    abstract public void printHelp();

    abstract void doAction() throws BibleStorageException, BibleExporterException, BibleImporterException;
    
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
    protected boolean isArgument(String word) {
	return word.startsWith("-");
    }
    
    protected String getFirstValue(String[] args) {
	if (args == null || args.length < 1) {
	    throw new IllegalArgumentException("No argument provided.");
	}
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
            retrieveSpecificVersions(args, versionList);
        }
        else {
	    List<BibleVersion> versionsRetrieved = retrieveAllVersions();
            versionList.add(versionsRetrieved.get(0));
        }
        return versionList;
    }
    
    protected List<BibleVersion> parseVersionsAndReturnAllIfEmpty(String[] args) throws BibleStorageException {
        List<BibleVersion> versionList = new ArrayList<BibleVersion>();
        if (isArgumentPresent(BIBLE_VERSION_ARGUMENT, args)) {
            retrieveSpecificVersions(args, versionList);
        }
        else
            versionList = retrieveAllVersions();
        return versionList;
    }
    
    protected List<BibleVersion> parseVersionsAndReturnNoneIfEmpty(String[] args) throws BibleStorageException {
        List<BibleVersion> versionList = new ArrayList<BibleVersion>();
        if (isArgumentPresent(BIBLE_VERSION_ARGUMENT, args)) {
            retrieveSpecificVersions(args, versionList);
        }
        return versionList;
    }

    private List<BibleVersion> retrieveAllVersions() throws BibleStorageException {
	List<BibleVersion> versionsRetrieved = bibleStorage.getAllBibleVersions();
	if (versionsRetrieved.size() < 1)
	    throw new RuntimeException("No Bible version found.");
	return versionsRetrieved;
    }

    private void retrieveSpecificVersions(String[] args, List<BibleVersion> versionList) throws BibleStorageException {
	for (String versionAbbr : getAllValuesOfArgument(BIBLE_VERSION_ARGUMENT, args)) {
	    BibleVersion v = bibleStorage.getBibleVersion(versionAbbr);
	    if (v == null)
		throw new IllegalArgumentException("No Bible version for abbreviation '" + versionAbbr + "' found.");
	    versionList.add(v);
	}
	if (versionList.size() < 1)
	    throw new RuntimeException("No Bible version found.");
    }

    //TODO merge with method getFirsValueOfArgument() and remove side effects
    protected String parseAddText(String[] args) {
        List<String> text = getAllValuesOfArgument(ADD_ARGUMENT , args);
        if (text.isEmpty())
            return null;
        else
            return text.get(0);
    }
    
    

}

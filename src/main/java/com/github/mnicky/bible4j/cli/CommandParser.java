package com.github.mnicky.bible4j.cli;

import java.util.ArrayList;
import java.util.List;

import com.github.mnicky.bible4j.Utils;
import com.github.mnicky.bible4j.data.BibleBook;
import com.github.mnicky.bible4j.data.BibleVersion;
import com.github.mnicky.bible4j.data.Position;
import com.github.mnicky.bible4j.parsers.BibleExporterException;
import com.github.mnicky.bible4j.parsers.BibleImporterException;
import com.github.mnicky.bible4j.storage.BibleStorage;
import com.github.mnicky.bible4j.storage.BibleStorageException;

public abstract class CommandParser {
    
    protected static final String BIBLE_VERSION_ARGUMENT = "-v";
    protected static final String BIBLE_BOOK_ARGUMENT = "-b";
    protected static final String ADD_ARGUMENT = "-add";
    
    protected final BibleStorage bibleStorage;
    protected boolean wholeChaptersRequested = false;
    
    public CommandParser(BibleStorage bibleStorage) {
	this.bibleStorage = bibleStorage;
    }

    public abstract void parse(String[] args) throws BibleStorageException, BibleImporterException, BibleExporterException;
    
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
    protected boolean isArgument(String word) {
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
            retrieveVersions(args, versionList);
        }
        else 
            versionList.add(bibleStorage.getAllBibleVersions().get(0));
        return versionList;
    }
    
    protected List<BibleVersion> parseVersionsAndReturnAllIfEmpty(String[] args) throws BibleStorageException {
        List<BibleVersion> versionList = new ArrayList<BibleVersion>();
        if (isArgumentPresent(BIBLE_VERSION_ARGUMENT, args)) {
            retrieveVersions(args, versionList);
        }
        else 
            versionList = (bibleStorage.getAllBibleVersions());
        return versionList;
    }
    
    protected List<BibleVersion> parseVersionsAndReturnNoneIfEmpty(String[] args) throws BibleStorageException {
        List<BibleVersion> versionList = new ArrayList<BibleVersion>();
        if (isArgumentPresent(BIBLE_VERSION_ARGUMENT, args)) {
            retrieveVersions(args, versionList);
        }
        return versionList;
    }

    protected List<Position> parsePositions(String posDef) {
        
	if (posDef.contains(":")) {
            posDef = posDef.replace(",", ".");
            posDef = posDef.replace(":", ",");
        }
        
        BibleBook book = extractBibleBook(posDef);
        List<Integer> chapters = parseChapters(posDef);
        List<Integer> verses = null;
        
        if (posDef.contains(","))
            verses = parseVerses(posDef);
        
        return getPositions(book, chapters, verses);	
    }

    private void retrieveVersions(String[] args, List<BibleVersion> versionList) throws BibleStorageException {
	for (String versionAbbr : getAllValuesOfArgument(BIBLE_VERSION_ARGUMENT, args)) {
	    BibleVersion v = bibleStorage.getBibleVersion(versionAbbr);
	    if (v == null)
		throw new IllegalArgumentException("Bible book abbreviation '" + versionAbbr + "' is unknown.");
	    versionList.add(v);
	}
    }

    private List<Position> getPositions(BibleBook book, List<Integer> chapters, List<Integer> verses) {
        List<Position> positionList = new ArrayList<Position>();
        
        //contains also verse numbers (i.e.: Jn3,5-7)
        if (verses != null) {
            assert chapters.size() == 1 : "bible coordinate definition contains verse numbers and more than one chapter number";
            for (int verse : verses)
        	positionList.add(new Position(book, chapters.get(0), verse));
        }
        //contains only chapter numbers (i.e.: Mt3-4)
        else {
            wholeChaptersRequested = true;
            for (int chapter : chapters)
        	positionList.add(new Position(book, chapter, 0));
        }
        
        
        return positionList;
    }

    protected List<Integer> parseVerses(String posDef) {
        List<Integer> verses;
        String verseDef = posDef.substring(posDef.indexOf(",") + 1);
        
        String[] verseRanges = null;
            //contains also disjoint verse nums (i.e.: Lk1-6.8)
            if (verseDef.contains("."))
        	 verseRanges = verseDef.split("\\.");
            else {
        	verseRanges = new String[1];
        	verseRanges[0] = verseDef;
            }
            verses = parseNumberRanges(verseRanges);
        
        return verses;
    }

    protected List<Integer> parseChapters(String posDef) {
        List<Integer> chapters;
        String chaptDef = posDef.substring(getPositionAfterBookName(posDef));
        
        //contains also verse numbers (i.e.: Jn3,4-6)
        if (chaptDef.contains(",")) {
            chapters = new ArrayList<Integer>(1);
            chapters.add(Integer.valueOf(chaptDef.substring(0, chaptDef.indexOf(","))));
        }
        
        //contains only chapter numbers  (i.e.: Mk4-6)
        else {
            String[] chaptRanges = null;
            //contains also disjoint chapter nums (i.e.: Mk3-5.8-9.15)
            if (chaptDef.contains("."))
        	 chaptRanges = chaptDef.split("\\.");
            else {
        	chaptRanges = new String[1];
        	chaptRanges[0] = chaptDef;
            }
            chapters = parseNumberRanges(chaptRanges);
        }
        
        if (chapters.size() < 1)
            throw new IllegalArgumentException("Bible coordinate doesn't contain chapter number(s).");
        
        return chapters;
    }

    protected List<Integer> parseNumberRanges(String[] numberRanges) {
        List<Integer> numbers = new ArrayList<Integer>();
        
        for (String numberRange : numberRanges) {
            
            //contains more numbers (i.e.: Mt13-15)
            if (numberRange.contains("-")) {
        	String[] numberRangeEnds = numberRange.split("-");
        	
        	if (numberRangeEnds.length > 2)
        	    throw new IllegalArgumentException("Bad format of number range.");
        	
        	int beginning = Integer.valueOf(numberRangeEnds[0]);
        	int end = Integer.valueOf(numberRangeEnds[1]);
        	
        	if (beginning > end)
        	    throw new IllegalArgumentException("Beginning of interval is greater than end: " + numberRange);
        	
        	for (int i = beginning; i <= end; i++)
        	    numbers.add(i);
        	
            }
            
            //contains only one number (i.e.: Jn15)
            else
        	numbers.add(Integer.valueOf(numberRange));
        }
        
        return numbers;
    }

    protected BibleBook extractBibleBook(String posDef) {
        String bookNameDef = extractFirstWord(posDef);
        return Utils.getBibleBookNameByAbbr(bookNameDef);
    }

    private int getPositionAfterBookName(String posDef) {
        int positionAfterBookName;
        
        if (Character.isDigit(posDef.charAt(0)))
            positionAfterBookName = getPositionOfFirstNonLetter(posDef.substring(1)) + 1;
        else
            positionAfterBookName = getPositionOfFirstNonLetter(posDef);
        
        return positionAfterBookName;
    }

    protected int getPositionOfFirstNonLetter(String posDef) {
        int firstNonLetterPosition = -1;
        for (int i = 0; i < posDef.length(); i++)
            if (!Character.isLetter(posDef.charAt(i))) {
        	firstNonLetterPosition = i;
        	break;
            }
        if (firstNonLetterPosition == -1)
            throw new IllegalArgumentException("Bible coordinate doesn't contain a book name.");	
        return firstNonLetterPosition;
    }

    protected String extractFirstWord(String posDef) {
        return posDef.substring(0, getPositionAfterBookName(posDef));
    }

    protected String parseText(String[] args) {
        List<String> text = getAllValuesOfArgument(ADD_ARGUMENT , args);
        if (text.isEmpty())
            return null;
        else
            return text.get(0);
    }
    
    

}

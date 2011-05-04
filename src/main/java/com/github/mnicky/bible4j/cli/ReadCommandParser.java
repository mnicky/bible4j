package com.github.mnicky.bible4j.cli;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.github.mnicky.bible4j.data.BibleBook;
import com.github.mnicky.bible4j.data.BibleVersion;
import com.github.mnicky.bible4j.data.Position;
import com.github.mnicky.bible4j.storage.BibleStorage;
import com.github.mnicky.bible4j.storage.BibleStorageException;
import com.github.mnicky.bible4j.Utils;

public final class ReadCommandParser extends CommandParser {

    private List<Position> positions;
    
    private List<BibleVersion> versions;
    
    
    public ReadCommandParser(BibleStorage bibleStorage) {
	super(bibleStorage);
	// TODO Auto-generated constructor stub
    }
    

    @Override
    public void run(String[] args) throws BibleStorageException {
	parseArgs(args);
    }

    private void parseArgs(String[] args) throws BibleStorageException {
	parseVersions(args);
	parsePositions(getFirstValue(args).toLowerCase(new Locale("en")));
    }

    private void parsePositions(String posDef) {
	posDef.replace(":", ",");
	BibleBook book = extractBibleBook(posDef);
	//TODO body
    }


    private BibleBook extractBibleBook(String posDef) {
	String bookNameDef = extractFirstWord(posDef);
	return Utils.getBibleBookNameByAbbr(bookNameDef);
    }


    private String extractFirstWord(String posDef) {
	int firstNonLetterPosition = -1;
	
	for (int i = 0; i < posDef.length(); i++) {
	    if (!Character.isLetter(posDef.charAt(i))) {
		firstNonLetterPosition = i;
		break;
	    }
	}
	
	if (firstNonLetterPosition == -1)
	    throw new IllegalArgumentException("Bible coordinate doesn't contain book name.");
	
	return posDef.substring(0, firstNonLetterPosition);
    }


    private void parseVersions(String[] args) throws BibleStorageException {
	if (isArgumentPresent("-v", args)) {
	    versions = new ArrayList<BibleVersion>();
	    for (String version : getAllValuesOfArgument("-v", args))
		versions.add(bibleStorage.getBibleVersion(version));
	}
    }
    
    
    //for testing purposes
    public static void main(String[] args) {
	ReadCommandParser p = new ReadCommandParser(null);
    }

}

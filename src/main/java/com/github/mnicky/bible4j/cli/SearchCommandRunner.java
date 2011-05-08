package com.github.mnicky.bible4j.cli;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.github.mnicky.bible4j.Utils;
import com.github.mnicky.bible4j.data.BibleBook;
import com.github.mnicky.bible4j.data.BibleVersion;
import com.github.mnicky.bible4j.data.Verse;
import com.github.mnicky.bible4j.storage.BibleStorage;
import com.github.mnicky.bible4j.storage.BibleStorageException;
import com.github.mnicky.bible4j.storage.H2DbBibleStorage;

class SearchCommandRunner extends CommandRunner {
    
    private static final String BIBLE_BOOK_PARAMETER = "-b";
    private List<BibleVersion> versions;
    private List<BibleBook> books;
    private String searchPhrases;
    private List<Verse> verses = null;

    public SearchCommandRunner(BibleStorage bibleStorage) {
	super(bibleStorage);
    }

    @Override
    void parseCommandLine(String[] args) throws BibleStorageException {
	versions = parseVersionsAndReturnNoneIfEmpty(args);
	books = parseBooks(args);
	searchPhrases = parseSearchPhrases(args);
    }
    
    @Override
    void doAction() throws BibleStorageException {
	verses = getVerses();
	displayFoundVerses();
    }

    private void displayFoundVerses() {
	if (verses == null)
	    return;
	
	if (verses.size() < 1) {
	    System.out.println("Specified phrase not found.");
	    return;
	}
	
	for (Verse verse : verses) {
	    System.out.println(verse.getBibleVersion().getAbbr() + "  " + formatPosition(verse) + "\t" + verse.getText());
	}
	System.out.println("\n" + verses.size() + " occurences total.");
    }

    private String formatPosition(Verse verse) {
	StringBuilder pos = new StringBuilder(verse.getPosition().toString()); 
	while(pos.length() < 12)
	    pos = pos.append(" ");
	return pos.toString();
    }

    private List<Verse> getVerses() throws BibleStorageException {
	List<Verse> verseList = new ArrayList<Verse>();
	
	if (versions.isEmpty() && books.isEmpty())
	    verseList = bibleStorage.searchVersesForText(searchPhrases);
	
	else if (versions.isEmpty() && !books.isEmpty())
	    for (BibleBook book : books)
		verseList.addAll(bibleStorage.searchVersesForText(searchPhrases, book));
	
	else if (!versions.isEmpty() && books.isEmpty())
	    for (BibleVersion version : versions)
		verseList.addAll(bibleStorage.searchVersesForText(searchPhrases, version));
		
	else if (!versions.isEmpty() && !books.isEmpty())
	    for (BibleVersion version : versions)
		for (BibleBook book : books)
		    verseList.addAll(bibleStorage.searchVersesForText(searchPhrases, book, version));
	
	return verseList;
    }

    private String parseSearchPhrases(String[] args) {
	String searchPhrases = "";
	List<String> phrases = getAllNonArgumentValues(args);
	
	for (String phrase : phrases) {
	    if (searchPhrases.equals(""))
	    	searchPhrases = phrase;
	    else 
		searchPhrases = searchPhrases.concat(" " + phrase);
	}
	
	return searchPhrases;
    }

    private List<BibleBook> parseBooks(String[] args) {
	List<BibleBook> bookList = new ArrayList<BibleBook>();
        if (isArgumentPresent(BIBLE_BOOK_PARAMETER, args)) {
            for (String abbr : getAllValuesOfArgument(BIBLE_BOOK_PARAMETER, args))
        	bookList.add(Utils.getBibleBookNameByAbbr(abbr));
        }
        return bookList;
    }

    @Override
    public void printHelp() {
	System.out.println();
	System.out.println("Usage:");
        System.out.println("\t" + CommandParser.BIBLE_SEARCH_COMMAND + " PHRASE_TO_SEARCH... [" + BIBLE_BOOK_ARGUMENT + " BIBLE_BOOK...] [" + BIBLE_VERSION_ARGUMENT + " BIBLE_VERSION...]");
        
        System.out.println();
        System.out.println("\tPHRASE_TO_SEARCH \t Phrase to search for (case is ignored)");
        System.out.println("\tBIBLE_BOOK \t\t Bible book (name or abbreviation)");
        System.out.println("\tBIBLE_VERSION \t\t Bible version abbreviation");

        System.out.println();
        System.out.println("\tYou can search for one word or for occurences of all specified words.");
        System.out.println("\tTo search only in specific Bible books, use argument '" + BIBLE_BOOK_ARGUMENT + "' and specify one or more Bible books.");
        System.out.println("\tTo search only in specific Bible versions, use argument '" + BIBLE_VERSION_ARGUMENT + "' and specify one or more Bible versions.");
        System.out.println("\tIt can be mixed freely altogether.");
        
        System.out.println();
        System.out.println("Examples:");
        
        System.out.println();
        System.out.println("  Simple search for one word:");
        System.out.println();
        System.out.println("\t" + CommandParser.BIBLE_SEARCH_COMMAND + " israel");
        
        System.out.println();
        System.out.println("  Search for occurences of all specified words:");
        System.out.println();
        System.out.println("\t" + CommandParser.BIBLE_SEARCH_COMMAND + " jesus john");
        
        System.out.println();
        System.out.println("  Search only in specific Bible books:");
        System.out.println();
        System.out.println("\t" + CommandParser.BIBLE_SEARCH_COMMAND + " egypt " + BIBLE_BOOK_ARGUMENT + " exodus");
        System.out.println("\t" + CommandParser.BIBLE_SEARCH_COMMAND + " jesus peter " + BIBLE_BOOK_ARGUMENT + " mk jn");
    
        System.out.println();
        System.out.println("  Search only in specific Bible versions:");
        System.out.println();
        System.out.println("\t" + CommandParser.BIBLE_SEARCH_COMMAND + " king " + BIBLE_VERSION_ARGUMENT + " kjv");
        System.out.println("\t" + CommandParser.BIBLE_SEARCH_COMMAND + " son of god " + BIBLE_VERSION_ARGUMENT + " rsv kjv web");
	
        System.out.println();
        System.out.println("  Mixed together:");
        System.out.println();
        System.out.println("\t" + CommandParser.BIBLE_SEARCH_COMMAND + " love " + BIBLE_BOOK_ARGUMENT + " 1jn " + BIBLE_VERSION_ARGUMENT + " kjv");
        System.out.println("\t" + CommandParser.BIBLE_SEARCH_COMMAND + " light of life " + BIBLE_BOOK_ARGUMENT + " john ps " + BIBLE_VERSION_ARGUMENT + " rsv kjv web");
        System.out.println();
    }
    
    
    public static void main(String[] args) throws BibleStorageException, SQLException {
	BibleStorage storage = new H2DbBibleStorage(DriverManager.getConnection("jdbc:h2:tcp://localhost/test", "test", ""));
	SearchCommandRunner p = new SearchCommandRunner(storage);
	String[] params2 = {"light of life", BIBLE_BOOK_ARGUMENT, "john", "ps", BIBLE_VERSION_ARGUMENT, "kjv", "asv", "rsv", "web"};
	p.parseCommandLine(params2);
	p.doAction();
	
    }

}

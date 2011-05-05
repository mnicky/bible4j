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

public class SearchCommandParser extends CommandParser {
    
    private static final String BIBLE_BOOK_PARAMETER = "-b";
    private List<BibleVersion> versions;
    private List<BibleBook> books;
    private String searchPhrases;

    public SearchCommandParser(BibleStorage bibleStorage) {
	super(bibleStorage);
    }

    @Override
    public void parse(String[] args) throws BibleStorageException {
	versions = parseVersionsAndReturnNoneIfEmpty(args);
	books = parseBooks(args);
	searchPhrases = parseSearchPhrases(args);
    }
    
    public List<Verse> getVerses() throws BibleStorageException {
	List<Verse> verses = new ArrayList<Verse>();
	
	if (versions.isEmpty() && books.isEmpty())
	    verses = bibleStorage.searchVersesForText(searchPhrases);
	
	else if (versions.isEmpty() && !books.isEmpty())
	    for (BibleBook book : books)
		verses.addAll(bibleStorage.searchVersesForText(searchPhrases, book));
	
	else if (!versions.isEmpty() && books.isEmpty())
	    for (BibleVersion version : versions)
		verses.addAll(bibleStorage.searchVersesForText(searchPhrases, version));
		
	else if (!versions.isEmpty() && !books.isEmpty())
	    for (BibleVersion version : versions)
		for (BibleBook book : books)
		    verses.addAll(bibleStorage.searchVersesForText(searchPhrases, book, version));
	
	return verses;
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
	System.out.println("Usage:");
        System.out.println("\t" + CommandParserLauncher.BIBLE_SEARCH_COMMAND + " PHRASE_TO_SEARCH... [" + BIBLE_BOOK_ARGUMENT + " BIBLE_BOOK...] [" + BIBLE_VERSION_ARGUMENT + " BIBLE_VERSION...]");
        
        System.out.println();
        System.out.println("\tPHRASE_TO_SEARCH Phrase to search for (case is ignored)");
        System.out.println("\tBIBLE_BOOK \t Bible book (name or abbreviation)");
        System.out.println("\tBIBLE_VERSION \t Bible version abbreviation");
        
        System.out.println();
        System.out.println("Examples:");
        
        System.out.println();
        System.out.println("  Simple search for one phrase:");
        System.out.println();
        System.out.println("\t" + CommandParserLauncher.BIBLE_SEARCH_COMMAND + " israel");
        
        System.out.println();
        System.out.println("  You can search for occurences of all specified phrases:");
        System.out.println();
        System.out.println("\t" + CommandParserLauncher.BIBLE_SEARCH_COMMAND + " jesus john");
        
        System.out.println();
        System.out.println("  You can specify one or more Bible books to search in:");
        System.out.println();
        System.out.println("\t" + CommandParserLauncher.BIBLE_SEARCH_COMMAND + " egypt " + BIBLE_BOOK_ARGUMENT + " exodus");
        System.out.println("\t" + CommandParserLauncher.BIBLE_SEARCH_COMMAND + " peter john " + BIBLE_BOOK_ARGUMENT + " mk jn");
    
        System.out.println();
        System.out.println("  You can specify one or more Bible versions to search in:");
        System.out.println();
        System.out.println("\t" + CommandParserLauncher.BIBLE_SEARCH_COMMAND + " king " + BIBLE_VERSION_ARGUMENT + " kjv");
        System.out.println("\t" + CommandParserLauncher.BIBLE_SEARCH_COMMAND + " son of god " + BIBLE_VERSION_ARGUMENT + " rsv kjv web");
	
        System.out.println();
        System.out.println("  And you can mix it freely together:");
        System.out.println();
        System.out.println("\t" + CommandParserLauncher.BIBLE_SEARCH_COMMAND + " love " + BIBLE_BOOK_ARGUMENT + " 1jn " + BIBLE_VERSION_ARGUMENT + " kjv");
        System.out.println("\t" + CommandParserLauncher.BIBLE_SEARCH_COMMAND + " light of life " + BIBLE_BOOK_ARGUMENT + " john ps " + BIBLE_VERSION_ARGUMENT + " rsv kjv web");
	
        
    }
    
    
    public static void main(String[] args) throws BibleStorageException, SQLException {
	BibleStorage storage = new H2DbBibleStorage(DriverManager.getConnection("jdbc:h2:tcp://localhost/test", "test", ""));
	SearchCommandParser p3 = new SearchCommandParser(storage);
	String[] params2 = {"light of life", BIBLE_BOOK_ARGUMENT, "john", "ps", BIBLE_VERSION_ARGUMENT, "kjv", "asv", "rsv", "web"};
	p3.parse(params2);
	System.out.println();
	List<Verse> verses = p3.getVerses(); 
	for (Verse v : verses)
	    System.out.println(v == null ? "no text found" : v.getText());
	
    }

}

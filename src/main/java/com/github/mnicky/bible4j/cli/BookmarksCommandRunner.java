package com.github.mnicky.bible4j.cli;

import static com.github.mnicky.bible4j.Utils.isWholeChapter;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.github.mnicky.bible4j.Utils;
import com.github.mnicky.bible4j.data.BibleVersion;
import com.github.mnicky.bible4j.data.Bookmark;
import com.github.mnicky.bible4j.data.Position;
import com.github.mnicky.bible4j.data.Verse;
import com.github.mnicky.bible4j.storage.BibleStorage;
import com.github.mnicky.bible4j.storage.BibleStorageException;
import com.github.mnicky.bible4j.storage.H2DbBibleStorage;

public class BookmarksCommandRunner extends CommandRunner {

    private List<Position> positions;
    
    private String nameOfBkmark = null;
    
    private List<BibleVersion> versions;
    
    private List<Bookmark> bookmarks;

    public BookmarksCommandRunner(BibleStorage bibleStorage) {
	super(bibleStorage);
    }

    @Override
    void parseCommandLine(String[] args) throws BibleStorageException {
	if (args.length > 0 && !isArgument(args[0]))
	    positions = Utils.parsePositions(getFirstValue(args).toLowerCase(new Locale("en")));
	if (isArgumentPresent(ADD_ARGUMENT, args)) 
	    nameOfBkmark = parseAddText(args);
	versions = parseVersionsAndReturnNoneIfEmpty(args);
    }
    
    @Override
    void doAction() throws BibleStorageException {
        retrieveOrAddBkmarks();
        //display
    }

    private void retrieveOrAddBkmarks() throws BibleStorageException {
	
	if (nameOfBkmark != null) {
	    if (positions.isEmpty())
		    throw new IllegalArgumentException("Coordinate of bookmark not specified");
	    if (versions.isEmpty())
		    throw new IllegalArgumentException("Bible version of bookmark not specified");
	    if (isWholeChapter(positions.get(0)))
		throw new IllegalArgumentException("Notes cannot be added to whole chapters.");
	    
	    bibleStorage.insertBookmark(new Bookmark(nameOfBkmark, new Verse("", positions.get(0), versions.get(0))));
	}
	else {
	    bookmarks = new ArrayList<Bookmark>();
	    
	    if (versions.isEmpty())
		bookmarks.addAll(bibleStorage.getBookmarks());
	    else
		for (BibleVersion version : versions) 
		    if (version != null)
		    bookmarks.addAll(bibleStorage.getBookmarks(version));
	}
    }
    
    private List<Bookmark> getBookmarks() {
	return bookmarks;
    }

    @Override
    public void printHelp() {
	System.out.println("Usage:");
        System.out.println("\t" + CommandParser.BOOKMARKS_COMMAND + " [" + BIBLE_VERSION_ARGUMENT + " BIBLE_VERSION...]");
        System.out.println("\t" + CommandParser.BOOKMARKS_COMMAND + " POSITION " + BIBLE_VERSION_ARGUMENT + " BIBLE_VERSION " + ADD_ARGUMENT + " BOOKMARK_NAME");
        
        System.out.println();
        System.out.println("\tBIBLE_VERSION \t Bible version abbreviation");
        System.out.println("\tPOSITION \t Bible coordinates without spaces");
        System.out.println("\tBOOKMARK_NAME \t Name of bookmark to add");
        
        System.out.println();
        System.out.println("\tTo view bookmarks for some Bible version(s) only,  use argument '" + BIBLE_VERSION_ARGUMENT + "' and specify one or more Bible versions.");
        System.out.println("\tTo add bookmark, specify Bible version and name of bookmark with arguments '" + BIBLE_VERSION_ARGUMENT + "' and '" + ADD_ARGUMENT + "'.");
        System.out.println("\tBookmarks can only be added to one verse. If more verses are specified, the first one is used.");
        System.out.println("\tSee '" + CommandParser.HELP_COMMAND + " " + CommandParser.BIBLE_READ_COMMAND + "' for description of how to define Bible coordinates.");
        
        System.out.println();
        System.out.println("Examples:");
        
        System.out.println();
        System.out.println("  View all bookmarks:");
        System.out.println();
        System.out.println("\t" + CommandParser.BOOKMARKS_COMMAND);
        
        System.out.println();
        System.out.println("  View bookmarks for specified Bible book(s):");
        System.out.println();
        System.out.println("\t" + CommandParser.BOOKMARKS_COMMAND + " " + BIBLE_VERSION_ARGUMENT + " kjv");
        System.out.println("\t" + CommandParser.BOOKMARKS_COMMAND + " " + BIBLE_VERSION_ARGUMENT + " niv rsv");
        
        System.out.println();
        System.out.println("  Add bookmark to verse:");
        System.out.println();
        System.out.println("\t" + CommandParser.BOOKMARKS_COMMAND + " Lk3:12 " + " " + BIBLE_VERSION_ARGUMENT + " kjv " + ADD_ARGUMENT + " \"This is bookmark name\"");    
	
    }


    //for testing purposes
    public static void main(String[] args) throws BibleStorageException, SQLException {
	BibleStorage storage = new H2DbBibleStorage(DriverManager.getConnection("jdbc:h2:tcp://localhost/test", "test", ""));
	BookmarksCommandRunner p = new BookmarksCommandRunner(storage);
	
//	String[] params = {"Jn1,1", ADD_ARGUMENT, "Jn 1,1 bookmark", BIBLE_VERSION_ARGUMENT, "kjv"};
//	p.parse(params);
//	p.retrieveOrAddBkmarks();
	
	String[] params2 = {"-v", "czecep", "kjv"};
	p.parseCommandLine(params2);
	p.retrieveOrAddBkmarks();
	System.out.println();
	List<Bookmark> bkmarks = p.getBookmarks();
	for (Bookmark b : bkmarks)
	    System.out.println(b);
    }

}

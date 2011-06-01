package com.github.mnicky.bible4j.cli;

import static com.github.mnicky.bible4j.Utils.isWholeChapter;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mnicky.bible4j.AppRunner;
import com.github.mnicky.bible4j.Utils;
import com.github.mnicky.bible4j.data.BibleVersion;
import com.github.mnicky.bible4j.data.Bookmark;
import com.github.mnicky.bible4j.data.Position;
import com.github.mnicky.bible4j.data.Verse;
import com.github.mnicky.bible4j.storage.BibleStorage;
import com.github.mnicky.bible4j.storage.H2DbBibleStorage;

/**
 *  This class controls the part of the application functionality which works with Bookmarks. 
 */
class BookmarksCommandRunner extends CommandRunner {
    
    private final static Logger logger = LoggerFactory.getLogger(AppRunner.AppLogger.class);

    private List<Position> positions;
    
    private String nameOfBookmark;
    
    private List<BibleVersion> versions;
    
    private List<Bookmark> bookmarks;
    
    private boolean deletingRequested;

    public BookmarksCommandRunner(BibleStorage bibleStorage) {
	super(bibleStorage);
    }

    @Override
    void parseCommandLine(String[] args) {
	if (args.length > 0 && !isArgument(args[0]))
	    positions = Utils.parsePositions(getFirstValue(args).toLowerCase(new Locale("en")));
	if (isArgumentPresent(ADD_ARGUMENT, args)) 
	    nameOfBookmark = getFirstValueOfArgument(ADD_ARGUMENT, args);
	if (isArgumentPresent(DELETE_ARGUMENT, args)) {
	    nameOfBookmark = getFirstValueOfArgument(DELETE_ARGUMENT, args);
	    deletingRequested = true;
	}
	versions = parseVersionsAndReturnNoneIfEmpty(args);
    }
    
    @Override
    void doRequestedAction() {
	if (nameOfBookmark != null) {
	    if (deletingRequested)
		deleteBookmarks();
	    else
		insertBookmark();
	}
	else {
	    retrieveBookmarks();
	    displayBookmarks();
	}
    }

    private void insertBookmark() {
	if (positions == null || positions.isEmpty()) {
	    logger.error("Empty list of Bible coordinates for Bookmark");
	    throw new IllegalArgumentException("Coordinate of bookmark not specified or in bad format");
	}
	if (versions == null || versions.isEmpty()) {
	    logger.error("Empty list of Bible versions for Bookmark");
	    throw new IllegalArgumentException("Bible version of bookmark not specified or in bad format");
	}
	if (isWholeChapter(positions.get(0))) {
	    logger.error("Whole chapters are specified in the coordinates for notes: {}", positions);
	    throw new IllegalArgumentException("Notes cannot be added to whole chapters.");
	}

	bibleStorage.insertBookmark(new Bookmark(nameOfBookmark, new Verse("", positions.get(0), versions.get(0))));
	System.out.println("Bookmark inserted.");
    }

    private void deleteBookmarks() {
	int bookmarksDeleted = bibleStorage.deleteBookmark(nameOfBookmark);
	System.out.println(bookmarksDeleted + " bookmark(s) deleted.");
    }

    private void retrieveBookmarks() {
	bookmarks = new ArrayList<Bookmark>();

	if (versions.isEmpty())
	    bookmarks.addAll(bibleStorage.getBookmarks());
	else
	    for (BibleVersion version : versions)
		if (version != null)
		    bookmarks.addAll(bibleStorage.getBookmarks(version));
    }

    private void displayBookmarks() {
	if (bookmarks == null)
	    return;
	
	if (bookmarks.size() < 1) {
	    System.out.println("No bookmarks found.");
	    return;
	}
	
	System.out.println("Saved bookmarks:");
	System.out.println();
	System.out.println("Bible \t Coordinate \t Name");
	System.out.println("------------------------------------------------------------");
	for (Bookmark bkmark : bookmarks)
	    System.out.println(bkmark.getVerse().getBibleVersion().getAbbr() + " \t " + bkmark.getVerse().getPosition() + " \t " + bkmark.getName());
    }

    @Override
    public void printHelp() {
	System.out.println();
	System.out.println("Usage:");
        System.out.println("\t" + CommandParser.BOOKMARKS_COMMAND + " [" + BIBLE_VERSION_ARGUMENT + " BIBLE_VERSION...]");
        System.out.println("\t" + CommandParser.BOOKMARKS_COMMAND + " POSITION " + BIBLE_VERSION_ARGUMENT + " BIBLE_VERSION " + ADD_ARGUMENT + " BOOKMARK_NAME");
        System.out.println("\t" + CommandParser.BOOKMARKS_COMMAND + " " + DELETE_ARGUMENT + " BOOKMARK_NAME");
        
        System.out.println();
        System.out.println("\tBIBLE_VERSION \t Bible version abbreviation");
        System.out.println("\tPOSITION \t Bible coordinates without spaces");
        System.out.println("\tBOOKMARK_NAME \t Name of bookmark to add");
        
        System.out.println();
        System.out.println("\tTo view bookmarks for some Bible version(s) only,  use argument '" + BIBLE_VERSION_ARGUMENT + "' and specify one or more Bible versions.");
        System.out.println("\tTo add bookmark, specify Bible version and name of bookmark with arguments '" + BIBLE_VERSION_ARGUMENT + "' and '" + ADD_ARGUMENT + "'.");
        System.out.println("\tTo delete bookmark, specify the name of the bookmark with argument '" + DELETE_ARGUMENT + "'. All bookmarks with specified name will be deleted.");
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
        
        System.out.println();
        System.out.println("  Delete bookmark:");
        System.out.println();
        System.out.println("\t" + CommandParser.BOOKMARKS_COMMAND + " " + DELETE_ARGUMENT + " \"The bookmark name\"");    
        System.out.println();
    }


    //for testing purposes
    public static void main(String[] args) throws SQLException {
	BibleStorage storage = new H2DbBibleStorage(DriverManager.getConnection("jdbc:h2:tcp://localhost/test", "test", ""));
	BookmarksCommandRunner p = new BookmarksCommandRunner(storage);
	
//	String[] params = {"Jn1,1", ADD_ARGUMENT, "Jn 1,1 bookmark", BIBLE_VERSION_ARGUMENT, "kjv"};
	String[] params2 = {"-v", "czecep", "kjv"};
	p.parseCommandLine(params2);
	p.doRequestedAction();
    }

}

package com.github.mnicky.bible4j.cli;

import java.util.Arrays;

import com.github.mnicky.bible4j.storage.BibleStorage;
import com.github.mnicky.bible4j.storage.BibleStorageException;

public class MainCommandParser {
    
    private final BibleStorage storage;
    
    private final String BIBLE_READ_COMMAND = "read";
    private final String BIBLE_SEARCH_COMMAND = "search";
    private final String IMPORT_COMMAND = "import";
    private final String EXPORT_COMMAND = "export";
    private final String NOTES_COMMAND = "note";
    private final String DICTIONARY_COMMAND = "dict";
    private final String DAILY_READINGS_COMMAND = "daily";
    private final String BOOKMARKS_COMMAND = "bkmark";
    private final String HELP_COMMAND = "help";
    
    public MainCommandParser(BibleStorage bibleStorage) {
	this.storage = bibleStorage;
    }
    
    public void parse(String[] args) throws BibleStorageException {
	
	CommandParser parser = getCommandParser(args);
	
	if (parser != null)
	    parser.run(Arrays.copyOfRange(args, 1, args.length));
	else
	    printHelp();
    }



    private CommandParser getCommandParser(String[] args) {
	
	if (args[0].equalsIgnoreCase(BIBLE_READ_COMMAND))
	    return new ReadCommandParser(storage);
	
	else if (args[0].equalsIgnoreCase(BIBLE_SEARCH_COMMAND))
	    return new SearchCommandParser(storage);
	
	else if (args[0].equalsIgnoreCase(IMPORT_COMMAND))
	    return new ImportCommandParser(storage);
	
	else if (args[0].equalsIgnoreCase(EXPORT_COMMAND))
	    return new ExportCommandParser(storage);
	
	else if (args[0].equalsIgnoreCase(NOTES_COMMAND))
	    return new NotesCommandParser(storage);
	
	else if (args[0].equalsIgnoreCase(DICTIONARY_COMMAND))
	    return new DictionaryCommandParser(storage);
	
	else if (args[0].equalsIgnoreCase(DAILY_READINGS_COMMAND))
	    return new DailyReadingsCommandParser(storage);
	
	else if (args[0].equalsIgnoreCase(BOOKMARKS_COMMAND))
	    return new BookmarksCommandParser(storage);
	
	else if (args[0].equalsIgnoreCase(HELP_COMMAND))
	    return new HelpCommandParser(storage);
	
	else
	    return null;
    }



    private void printHelp() {
	System.out.println("Use argument " + HELP_COMMAND + " for help.");
    }
    
}
